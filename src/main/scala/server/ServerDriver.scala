package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import server.actors.{AuthActor, DatabaseManagementActor}

object ServerDriver extends App {
  val config = ConfigFactory.load()
  val system: ActorSystem =
    ActorSystem("ServerSystem", config.getConfig("serverConf").withFallback(config))

  val databaseActorRef: ActorRef = system.actorOf(DatabaseManagementActor.props(), "DbActor")
  val authActorRef: ActorRef = system.actorOf(AuthActor.props(databaseActorRef), "AuthActor")
  println(databaseActorRef.path)
  println(authActorRef.path)

}
