package ovens

import akka.actor.{Actor, ActorSelection}
import ovens.Cooker.Tick
import ovens.Oven.{Power, Temp}
import ovens.Thermostat.SetTemp

object Thermostat {
  case class SetTemp(temp: Long)
  val POWER: Int = 75000
}

class Thermostat extends Actor {
  var ovenSelection: ActorSelection = _
  var logger: ActorSelection = _
  var setPoint: Long = Oven.defaultAmbient
  var lastTick: Long = System.currentTimeMillis()
  var powerOn: Boolean = false

  override def preStart(): Unit = {
    super.preStart()
    context.system.eventStream.subscribe(self, classOf[Tick])
    ovenSelection = context.actorSelection("akka://system/user/oven")
    logger = context.system.actorSelection("akka://system/user/logger")
  }

  def log(msg: String): Unit = logger ! msg

  override def receive: Receive = {
    // request temperature info and control power based on last reported
    case Tick(t) =>
      ovenSelection ! Temp(0)
      if (powerOn) ovenSelection ! Power(Thermostat.POWER * (t - lastTick) / 1000)
      lastTick = t

    // turn on or off the oven power
    case Temp(t) =>
      log(s"Reported temp is $t")
      powerOn = setPoint > t

    case SetTemp(t) =>
      log(s"Target temp set at $t")
      setPoint = t
      ovenSelection ! Temp(0)
  }
}
