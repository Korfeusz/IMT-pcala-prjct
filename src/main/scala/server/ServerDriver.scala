package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import server.actors.AuthActor

object ServerDriver extends App {

  val system: ActorSystem = ActorSystem("ServerSystem")


  val authActorRef: ActorRef = system.actorOf(AuthActor.props, "AuthActor")



  //#main-send-messages
//  howdyGreeter ! WhoToGreet("Akka")

}
