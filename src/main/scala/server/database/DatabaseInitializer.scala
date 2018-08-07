package server.database
import server.Password
import slick.jdbc.PostgresProfile.api._
import tables.{Data, Users}

object DatabaseInitializer {
  def apply(database: Database): DatabaseInitializer = new DatabaseInitializer(database)
}

class DatabaseInitializer(database: Database) {
  val users = TableQuery[Users]
  val data = TableQuery[Data]
  val adminHashAndSalt = Password.generateNewHashAndSalt("admin")
  val userHashAndSalt = Password.generateNewHashAndSalt("user")


  val setup = DBIO.seq(
    (users.schema ++ data.schema).create,
    users += (1, "admin",adminHashAndSalt("hash"), adminHashAndSalt("salt"), true, true),
    users += (2, "user", userHashAndSalt("hash"), userHashAndSalt("salt"), false, true),
    data ++= Seq(
      (1, "example1", "some example 1"),
      (2, "example2", "some example 2"),
      (3, "example3", "some example 3"),
      (4, "example4", "some example 4"),
    )
  )

  val setupFuture = database.run(setup)
}
