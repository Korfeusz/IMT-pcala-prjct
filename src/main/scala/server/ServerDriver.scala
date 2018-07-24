package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import server.actors.AuthActor

object ServerDriver extends App {

  val system: ActorSystem = ActorSystem("ServerSystem")

//  val databaseActorRef: ActorRef = system.actorOf()
//  val authActorRef: ActorRef = system.actorOf(AuthActor.props(), "AuthActor")



  //#main-send-messages
//  howdyGreeter ! WhoToGreet("Akka")

}
