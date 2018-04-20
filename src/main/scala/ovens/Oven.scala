package ovens

import akka.actor.{Actor, ActorSelection}
import ovens.Cooker.Tick
import ovens.Oven.{Power, Temp}

object Oven {
  case class Power(joules: Long)
  case class Temp(temp: Long)
  val defaultAmbient: Long = 20
}

// akka://system/user/oven
class Oven extends Actor {
  // Temperature calculation
  // energy = temp * HEAT_CAPACITY
  // Lost energy = temp-differential * seconds * LEAK_RATE
  val HEAT_CAPACITY: Long = 4000
  val LEAK_RATE: Long = 400
  var ambient: Long = Oven.defaultAmbient // celsius, initial
  var energy: Long = ambient * HEAT_CAPACITY

  var lastTick: Long = System.currentTimeMillis()

  var logger: ActorSelection = _

  def temp: Long = energy / HEAT_CAPACITY

  override def preStart(): Unit = {
    super.preStart()
    logger = context.system.actorSelection("akka://system/user/logger")
    context.system.eventStream.subscribe(self, classOf[Tick])
  }

  def log(msg: String): Unit = logger ! msg

  override def receive: Receive = {
    // simulate cooling
    case Tick(now: Long) =>
      energy -= (temp - ambient) * (LEAK_RATE * (now - lastTick)) / 1000
      lastTick = now
      log(s"Oven temp (cooling calc) is $temp")

    // simulate heating
    case Power(joules) => energy += joules
      log(s"Oven temp $temp")

    // report temp to requester (usually thermostat)
    case Temp(_) => sender() ! Temp(temp)
  }
}
