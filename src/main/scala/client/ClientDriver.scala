package client

import akka.actor.{ActorRef, ActorSystem, Address}
import client.actors.{ClientActor, PrinterActor}
import com.typesafe.config.ConfigFactory

object ClientDriver extends  App {
  val host = "127.0.0.1"
  val port = 2553
  val serverAddresses = ServerActorAddresses(host, port)

  val config = ConfigFactory.load()
  val system: ActorSystem =
    ActorSystem("ClientSystem", config.getConfig("clientConf").withFallback(config))


  val printerActorRef: ActorRef = system.actorOf(PrinterActor.props)
  val clientActorRef: ActorRef = system.actorOf(ClientActor.props(printerActorRef))

  val commandLineInterface: CommandLineInterface = CommandLineInterface(clientActorRef, printerActorRef, serverAddresses)
  commandLineInterface.startInputLoop()

}
