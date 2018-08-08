package server.database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object SysInternalDatabaseManager {
  def apply(database: Database): SysInternalDatabaseManager = new SysInternalDatabaseManager(database)
}


class SysInternalDatabaseManager(database: Database) {

  import Tables.users


  def addUser(username: String, hash: String, salt: String): Future[Unit] = {
    val insertAction = DBIO.seq(users += (0, username, hash, salt, None, false, false))
    database.run(insertAction)
  }

  def deleteUser(username: String): Future[Int] = {
    val q = users.filter(_.name === username)
    database.run(q.delete)
  }

  def addToken(username: String, tokenString: Option[String]): Future[Int] = {
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.update(tokenString))
  }

  def deleteToken(username: String): Future[Int] = {
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.update(None))
  }

  def activateUser(username: String): Future[Int] = {
    val q = for {c <- users if c.name === username} yield c.isAuthorized
    database.run(q.update(true))
  }

  def makeAdmin(username: String): Future[Int] = {
    val q = for {c <- users if c.name === username} yield c.isAdmin
    database.run(q.update(true))
  }

  def getToken(username: String): Future[Option[String]] = {
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.result.head)
  }

  def testIfAdmin(username: String): Future[Boolean] = {
    val q = for {c <- users if c.name === username} yield c.isAdmin
    database.run(q.result.head)
  }

  def testIfActive(username: String): Future[Boolean] = {
    val q = for {c <- users if c.name === username} yield c.isAuthorized
    database.run(q.result.head)
  }

  def getInactiveUsers: Future[Seq[String]] = {
    val q = for {c <- users if c.isAuthorized === false} yield c.name
    database.run(q.result)
  }

  def getAllUsers: Future[Seq[String]] = {
    val q = for {c <- users} yield c.name
    database.run(q.result)
  }

  def getUserCredentials(username: String): Future[(String, String, Boolean)] = {
    val q = for {c <- users if c.name === username} yield (c.passwordHash, c.salt, c.isAuthorized)
    database.run(q.result.head)

  }
}
