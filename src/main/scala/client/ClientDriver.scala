package client

import akka.actor.{ActorRef, ActorSystem}
import client.actors.{ClientActor, PrinterActor}
import com.typesafe.config.ConfigFactory

object ClientDriver extends App {

  val config = ConfigFactory.load()
  println(config)
  val system: ActorSystem =
    ActorSystem("ClientSystem", config.getConfig("clientConf").withFallback(config))

  val clientActorRef: ActorRef = system.actorOf(ClientActor.props(), "ClientActor")
  val printerActorRef: ActorRef = system.actorOf(PrinterActor.props)

  val commandLineInterface: CommandLineInterface = CommandLineInterface(clientActorRef, printerActorRef)
  commandLineInterface.startInputLoop()

}
