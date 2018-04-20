package akkastuff

import akka.actor.{Actor, ActorPath, ActorRef, ActorSelection, ActorSystem, PoisonPill, Props}
import akkastuff.MyActor.Msg

class OtherActor extends Actor {
  override def receive: Receive = {
    case "Ping" => sender ! "Pong"
    case "Who" => println(s"${self.path}")
    case "Make" => context.actorOf(Props[OtherActor], "my-child")
    case "Die" => println(s"${self.path} is dying"); context.system.stop(self)
  }
}

object MyActor {
  sealed case class Msg(s: String, c: Int)
}

class MyActor(other: ActorRef) extends Actor {
    override def receive: PartialFunction[Any, Unit] = new PartialFunction[Any, Unit](){
      override def isDefinedAt(x: Any): Boolean = x match {
        case Msg("P", _) => true
        case Msg(x, _) => true
        case "Goodbye" => true
        case "Pong" => true
        case _ => false
      }

      override def apply(v1: Any): Unit = v1 match {
        case Msg("P", _) => other ! "Ping"
        case Msg(x, _) => println(s"Hello... You sent me a message with $x ... Who said that")
        case "Pong" => println("Got a pong back...")
        case "Goodbye" => println("Noooo don't leave")
      }

    }
//  override def receive: Receive = {
//    case "Hello" => println("Hello... Who said that")
//    case "Goodbye" => println("Noooo don't leave")
//    case x => println(s"What does $x mean")
//  }
}

object SayHello {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("system")
    val other = system.actorOf(Props[OtherActor], "other")
    val ar = system.actorOf(Props(classOf[MyActor], other), "my-main-actor")

    ar ! "Goodbye"
    ar ! Msg("Hello", 3)
    ar ! Msg("Goodbye", 99)
    ar ! Msg("P", 0)

    other ! "Who"
    other ! "Make"
    val ap = ActorPath.fromString("akka://system/user/other/my-child")
    val as = system.actorSelection(ap)
    Thread.sleep(1000)
    as ! "Who"

    other ! PoisonPill
    other ! "Who"
//    other ! "Die"
//    other ! "Who"
//    as ! "Who"
  }
}
