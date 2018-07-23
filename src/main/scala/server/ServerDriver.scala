package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import client.CommandLineInterfaceActor

object ServerDriver extends App {

  val system: ActorSystem = ActorSystem("ServerSystem")

  //#create-actors
  // Create the printer actor
//  val printer: ActorRef = system.actorOf(c"printerActor")



  //#main-send-messages
//  howdyGreeter ! WhoToGreet("Akka")

}
