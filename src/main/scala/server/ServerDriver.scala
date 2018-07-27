package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import server.actors.{AuthActor, DatabaseManagementActor}

object ServerDriver extends App {

  val system: ActorSystem = ActorSystem("ServerSystem")

  val databaseActorRef: ActorRef = system.actorOf(DatabaseManagementActor.props(), "DbActor")
  val authActorRef: ActorRef = system.actorOf(AuthActor.props(databaseActorRef), "AuthActor")



  //#main-send-messages
//  howdyGreeter ! WhoToGreet("Akka")

}
