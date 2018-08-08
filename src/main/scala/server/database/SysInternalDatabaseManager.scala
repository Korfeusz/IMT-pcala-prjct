package server.database
import java.security.KeyStore.PasswordProtection

import server.Password
import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable
import scala.collection.mutable.Map
import scala.concurrent.Future

object SysInternalDatabaseManager {
  def apply(database: Database): SysInternalDatabaseManager = new SysInternalDatabaseManager(database)
}


class SysInternalDatabaseManager(database: Database) {

  import Tables.users


  def addUser(username: String, hash: String, salt: String) = {
    println("Adding user... ")
    val insertAction = DBIO.seq(users += (0, username, hash, salt, None, false, false))
    database.run(insertAction)
  }

  def deleteUser(username: String) = {
    val q = users.filter(_.name === username)
    database.run(q.delete)
  }

  def addToken(username: String, tokenString: Option[String]) = {
    println("Adding token")
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.update(tokenString))
  }

  def deleteToken(username: String) = {
    println("Deleting token")
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.update(None))
  }

  def activateUser(username: String) = {
    println("Activating user")
    val q = for {c <- users if c.name === username} yield c.isAuthorized
    database.run(q.update(true))
  }

  def makeAdmin(username: String) = {
    println("Adminifying")
    val q = for {c <- users if c.name === username} yield c.isAdmin
    database.run(q.update(true))
  }

  def getToken(username: String): Future[Option[String]] = {
    val q = for {c <- users if c.name === username} yield c.token
    database.run(q.result.head)
  }

  def testIfAdmin(username: String) = {
    println("Testing if Admin")
    val q = for {c <- users if c.name === username} yield c.isAdmin
    database.run(q.result.head)
  }

  def testIfActive(username: String) = {
    val q = for {c <- users if c.name === username} yield c.isAuthorized
    database.run(q.result.head)
  }

  def getInactiveUsers() = {
    val q = for {c <- users if c.isAuthorized === false} yield c.name
    database.run(q.result)
  }

  def getAllUsers() = {
    val q = for {c <- users} yield c.name
    database.run(q.result)
  }

  def getUserCredentials(username: String): Future[(String, String, Boolean)] = {
    val q = for {c <- users if c.name === username} yield (c.passwordHash, c.salt, c.isAuthorized)
    database.run(q.result.head)
  }
}
