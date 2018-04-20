package primes

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}

object PrimeActors {

  class PrimeActor extends Actor {
    var myNum: Long = _
    var next: ActorRef = _

    val regular: Receive = {
      case "Die" => next.tell("Die", sender)
      case n: Long => if (n % myNum != 0) next ! n
    }

    val awaitingNext: Receive = {
      case "Die" => sender ! "DEAD"
      case n: Long =>
        if (n % myNum != 0) {
          next = context.system.actorOf(Props[PrimeActor])
          context.become(regular)
        }
    }

    override def receive: Receive = {
      case n: Long =>
        println(s"$n is prime!")
        myNum = n
        context.become(awaitingNext)
      case "Die" => sender ! "DEAD"
    }
  }

  class RootActor extends Actor {
    val start = System.currentTimeMillis()
    val INPUT_LENGTH = 1000000L
    val numberTwo = context.system.actorOf(Props[PrimeActor])
    for (x <- 2L to INPUT_LENGTH) numberTwo ! x
    numberTwo ! "Die"

    override def receive: Receive = {
      case "DEAD" => {
        println(f"Time take for $INPUT_LENGTH is ${(System.currentTimeMillis() - start)/1000.0}%7.3f sec")
        context.system.terminate()
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("system")
    system.actorOf(Props[RootActor])
  }
}
