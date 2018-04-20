package ovens

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import ovens.Thermostat.SetTemp

import scala.concurrent.duration.FiniteDuration

object Cooker {
  sealed case class Tick(t: Long)

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("system")

    // Arrange timer ticks on system bus
    val interval = FiniteDuration(1000, TimeUnit.MILLISECONDS)
    val eventStream = system.eventStream
    import scala.concurrent.ExecutionContext.Implicits.global
    system.scheduler.schedule(interval, interval,
      () => eventStream.publish(Tick(System.currentTimeMillis()))
    )

    val loggerRef = system.actorOf(Props[LoggerActor], "logger")
    val ovenRef = system.actorOf(Props[Oven], "oven")
    val thermostatRef = system.actorOf(Props[Thermostat], "thermostat")

    // run temperature profile
    Thread.sleep(5000)
    thermostatRef ! SetTemp(100)
    Thread.sleep(30000)
    thermostatRef ! SetTemp(20)
    Thread.sleep(30000)
    system.terminate()
  }
}
