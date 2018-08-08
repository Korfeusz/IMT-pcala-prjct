package server.actors

import akka.actor.{Actor, ActorRef, Props}
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadData, SaveData}
import server.actors.messages.AuthToDatabaseMessages.{AddUser, DeleteToken}
import common.messages.CommonMessages._
import server.actors.messages.PasswordCheckToDatabaseMessages.{GetUserCredentials, UserCredentials}
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken
import server.database.SysInternalDatabaseManager
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object DatabaseManagementActor {
  def props(sysDbManager: SysInternalDatabaseManager): Props
  = Props(new DatabaseManagementActor(sysDbManager: SysInternalDatabaseManager))
}

class DatabaseManagementActor(sysDbManager: SysInternalDatabaseManager) extends Actor{
  import DatabaseManagementActor._
  import server.database.{SysInternalDatabaseManager, DatabaseManagement}

  val responses = Responses.DatabaseActorResponses(sysDbManager, self)

  override def receive: Receive = {
    case AddUser(username, encryptedPass, salt) =>
      sysDbManager.addUser(username, encryptedPass, salt)
    case DeleteToken(username) =>
      sysDbManager.deleteToken(username)
    case Token(username, tokenString) =>
      sysDbManager.addToken(username, tokenString)
    case GetUserCredentials(username) =>
      responses.handleGetUserCredentials(username, sender)
    case GetToken(username) =>
      responses.handleGetToken(username, sender)
    case ActorRefWrap(clientRef, message) => message match {
      case TokenWrap(message, token) =>
        context.actorOf(TokenCheckActor.props(token, message, self, self, clientRef))
    }
    case TokenCheckResult(senderName, message, result, clientRef) if result =>
      message match {
        case GetInactiveUsers  =>
          responses.handleGetInactiveUsers(clientRef, senderName)
        case GetAllUsers =>
          responses.handleGetAllUsers(clientRef, senderName)
        case ActivateUser(username) =>
          responses.handleActivateUser(clientRef, senderName, username)
        case MakeAdmin(username) =>
          responses.handleMakeAdmin(clientRef, senderName, username)
        case DeleteUser(username) =>
          responses.handleDeleteUser(clientRef, senderName, username)
        case SaveData(data, where) =>
          DatabaseManagement.saveData(data, where)
        case LoadData(where) =>
          DatabaseManagement.loadData(where)
        case DeleteData(where) =>
          DatabaseManagement.deleteData(where)
      }
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
