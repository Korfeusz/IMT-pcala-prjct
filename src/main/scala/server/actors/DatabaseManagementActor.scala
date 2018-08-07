package server.actors

import akka.actor.{Actor, ActorRef, Props}
import common.messages.AdminToDatabaseMessages.{ActivateUser, GetInactiveUsers, InactiveUsers}
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadData, SaveData}
import server.actors.messages.AuthToDatabaseMessages.{AddUser, DeleteToken}
import common.messages.CommonMessages._
import server.actors.messages.PasswordCheckToDatabaseMessages.{GetUserCredentials, UserCredentials}
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken

object DatabaseManagementActor {
  def props(): Props = Props(new DatabaseManagementActor())
}

class DatabaseManagementActor() extends Actor{
  import DatabaseManagementActor._
  import server.database.{SysInternalDatabaseManager, DatabaseManagement}

  override def receive: Receive = {
    case AddUser(username, encryptedPass, salt) =>
      SysInternalDatabaseManager.addUser(username, encryptedPass, salt)
    case DeleteToken(username) =>
      SysInternalDatabaseManager.deleteToken(username)
    case Token(username, tokenString) =>
      println("WWW")
      SysInternalDatabaseManager.addToken(username, tokenString)
    case GetUserCredentials(username) =>
      val cred = SysInternalDatabaseManager.getUserCredentials(username)
      sender ! UserCredentials(cred("hash").toString, cred("salt").toString, cred("active"))
    case GetToken(username) =>
      sender ! Token(username, SysInternalDatabaseManager.getToken(username).asInstanceOf[String])
    case ActorRefWrap(clientRef, message) => message match {
      case TokenWrap(message, token) =>
        context.actorOf(TokenCheckActor.props(token, message, self, self, clientRef))
    }
    case TokenCheckResult(senderName, message, result, clientRef) if result =>
      message match {
        case GetInactiveUsers if SysInternalDatabaseManager.testIfAdmin(senderName) =>
          sender ! InactiveUsers(SysInternalDatabaseManager.getInactiveUsers)
        case ActivateUser(username) if SysInternalDatabaseManager.testIfAdmin(senderName) =>
          SysInternalDatabaseManager.activateUser(username)
          // Send confirmation to user (username)
        case SaveData(data, where) =>
          DatabaseManagement.saveData(data, where)
        case LoadData(where) =>
          DatabaseManagement.loadData(where)
        case DeleteData(where) =>
          DatabaseManagement.deleteData(where)
      }
    case actor: ActorRef =>
      actor ! "I got it!"
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
