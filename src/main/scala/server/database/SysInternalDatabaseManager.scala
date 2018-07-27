package server.database
import scala.collection.mutable
import scala.collection.mutable.Map

object SysInternalDatabaseManager {
  var users : mutable.Map[String, mutable.Map[String, Any]] = mutable.Map()

  def addUser(username: String, hash: String, salt: String): Unit = {
    println("Adding user... ")
    val data = mutable.Map( "hash" -> hash,
                            "salt" -> salt,
                            "token" -> None,
                            "active" -> false,
                            "admin" -> false)
    users += (username -> data)
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

  def testIfAdmin(username: String): Boolean = {
    println("Testing if Admin")
    users(username)("admin").asInstanceOf[Boolean]
  }

  def testIfActive(username: String) : Boolean = {
    users(username)("active").asInstanceOf[Boolean]
  }
  def getInactiveUsers: collection.Set[String] = {
    users.filter(p => !p._2("active").asInstanceOf[Boolean]).keySet
  }


}
