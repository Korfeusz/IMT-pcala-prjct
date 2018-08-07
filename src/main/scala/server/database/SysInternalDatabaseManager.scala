package server.database
import java.security.KeyStore.PasswordProtection

import server.Password

import scala.collection.mutable
import scala.collection.mutable.Map

object SysInternalDatabaseManager {
  var users : mutable.Map[String, mutable.Map[String, Any]] = mutable.Map()
  generateAdmin()

  def generateAdmin(): Unit = {
    val adminHashAndSalt = Password.generateNewHashAndSalt("admin")
    addUser("admin", adminHashAndSalt("hash"), adminHashAndSalt("salt"))
    activateUser("admin")
    makeAdmin("admin")

  }
  def addUser(username: String, hash: String, salt: String): Unit = {
    println("Adding user... ")
    val data = mutable.Map( "hash" -> hash,
                            "salt" -> salt,
                            "token" -> None,
                            "active" -> false,
                            "admin" -> false)
    println(data)
    users += (username -> data)
  }

  def deleteUser(username: String): Unit = {
    users -= username
  }

  def addToken(username: String, tokenString: String) : Unit = {
    println("Adding token")
    users(username)("token") = tokenString
  }

  def deleteToken(username: String) : Unit = {
    println("Deleting token")
    users(username)("token") = None
  }

  def activateUser(username: String) : Unit = {
    println("Activating user")
    users(username)("active") = true
  }

  def makeAdmin(username: String) : Unit = {
    println("Adminifying")
    users(username)("admin") = true
  }

  def getToken(username: String) ={
    users(username)("token")
  }
  def testIfAdmin(username: String): Boolean = {
    println("Testing if Admin")
    users(username)("admin").asInstanceOf[Boolean]
  }

  def testIfActive(username: String) : Boolean = {
    users(username)("active").asInstanceOf[Boolean]
  }
  def getInactiveUsers: collection.Set[String] = {
    println(users.filter(p => !p._2("active").asInstanceOf[Boolean]).keySet)
    users.filter(p => !p._2("active").asInstanceOf[Boolean]).keySet
  }

  def getUserCredentials(username: String) = {
    println("User credentials accessed")
    users(username)
  }

}
