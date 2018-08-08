package server

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import server.actors.{AuthActor, DatabaseManagementActor}
import server.database.{DatabaseInitializer, DatabaseManagement, SysInternalDatabaseManager}

object ServerDriver extends App {
  val config = ConfigFactory.load()
  val system: ActorSystem =
    ActorSystem("ServerSystem", config.getConfig("serverConf").withFallback(config))

  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/mydb"
  val password = "password"
  val driver = "org.postgresql.Driver"
  val database = DatabaseInitializer(url, user = user, password = password, driver = driver).database
  val sysDbManager = SysInternalDatabaseManager(database)
  val dbManager = DatabaseManagement(database)

  val databaseActorRef: ActorRef = system.actorOf(DatabaseManagementActor.props(sysDbManager, dbManager), "DbActor")
  val authActorRef: ActorRef = system.actorOf(AuthActor.props(databaseActorRef), "AuthActor")

}

