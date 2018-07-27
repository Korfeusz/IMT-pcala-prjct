package server.actors

import akka.actor.{Actor, Props}
import common.messages.AdminToDatabaseMessages.{ActivateUser, GetInactiveUsers, InactiveUsers}
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadData, SaveData}
import server.actors.messages.AuthToDatabaseMessages.{AddUser, DeleteToken}
import common.messages.CommonMessages._

object DatabaseManagementActor {
  def props(): Props = Props(new DatabaseManagementActor())
}

class DatabaseManagementActor() extends Actor{
  import DatabaseManagementActor._
  import server.database.SysInternalDatabaseManager
  override def receive: Receive = {
    case AddUser(username, encryptedPass, salt) =>
      SysInternalDatabaseManager.addUser(username, encryptedPass, salt)
    case DeleteToken(username) =>
      SysInternalDatabaseManager.deleteToken(username)
    case Token(username, tokenString) =>
      SysInternalDatabaseManager.addToken(username, tokenString)
    case TokenWrap(message, token) =>
      context.actorOf(TokenCheckActor.props(token, message, self, self))
    case TokenCheckResult(senderName, message, result) if result && SysInternalDatabaseManager.testIfActive(senderName) =>
      message match {
        case GetInactiveUsers if SysInternalDatabaseManager.testIfAdmin(senderName) =>
          sender ! InactiveUsers(SysInternalDatabaseManager.getInactiveUsers)
        case ActivateUser(username) if SysInternalDatabaseManager.testIfAdmin(senderName) =>
          SysInternalDatabaseManager.activateUser(username)
          // Send confirmation to user (username)
        case SaveData(data, where) =>
          SysInternalDatabaseManager.saveData(data, where)
        case LoadData(where) =>
          SysInternalDatabaseManager.loadData(where)
        case DeleteData(where) =>
          SysInternalDatabaseManager.deleteData(where)
      }
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
