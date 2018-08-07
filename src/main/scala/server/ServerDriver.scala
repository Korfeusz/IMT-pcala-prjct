package server

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import server.actors.{AuthActor, DatabaseManagementActor}
import server.database.DatabaseInitializer
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object ServerDriver extends App {
  val config = ConfigFactory.load()
  val system: ActorSystem =
    ActorSystem("ServerSystem", config.getConfig("serverConf").withFallback(config))

  val databaseActorRef: ActorRef = system.actorOf(DatabaseManagementActor.props(), "DbActor")
  val authActorRef: ActorRef = system.actorOf(AuthActor.props(databaseActorRef), "AuthActor")
  println(databaseActorRef.path)
  println(authActorRef.path)

  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/mydb"
  val password = "password"
  val driver = "org.postgresql.Driver"
  val db = Database.forURL(url, user = user, password = password, driver = driver)
  val dbInitializer = DatabaseInitializer(db)


  println("Users:")
  db.run(dbInitializer.users.result).map(_.foreach {
    case (id, name, passwordHash, salt, isAdmin, isAuthorized) =>
      println("  " + id + " " + name+ " " + passwordHash+ " " + salt+ " " + isAdmin+ " " + isAuthorized)

  })
}

