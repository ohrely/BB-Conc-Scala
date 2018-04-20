package ovens

import akka.actor.Actor

// akka://system/user/logger
class LoggerActor extends Actor {
  override def receive: Receive = {
    case x: String => println(f"${sender.path.name}%12s LOG: $x")
  }
}
