package server.database
import server.Password
import slick.jdbc.PostgresProfile.api._
import tables.{Data, Users}

import scala.concurrent.Future

object DatabaseInitializer {
  def apply(url: String, user: String, password: String, driver: String): DatabaseInitializer
  = new DatabaseInitializer(url, user, password, driver)
}

class DatabaseInitializer(url: String, user: String, password: String, driver: String) {
  import Tables.{data, users}
  val database = Database.forURL(url, user = user, password = password, driver = driver)
  val adminHashAndSalt: Map[String, String] = Password.generateNewHashAndSalt("admin")
  val userHashAndSalt: Map[String, String] = Password.generateNewHashAndSalt("user")

  val setup = DBIO.seq(
    (users.schema ++ data.schema).create,
    users += (1, "admin",adminHashAndSalt("hash"), adminHashAndSalt("salt"), None, true, true),
    users += (2, "user", userHashAndSalt("hash"), userHashAndSalt("salt"), None, false, true),
    data ++= Seq(
      (1, "example1", "some example 1"),
      (2, "example2", "some example 2"),
      (3, "example3", "some example 3"),
      (4, "example4", "some example 4"),
    )
  )

  val setupFuture: Future[Unit] = database.run(setup)
}
