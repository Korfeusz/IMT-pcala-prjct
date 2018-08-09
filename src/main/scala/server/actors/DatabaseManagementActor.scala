package server.actors

import akka.actor.{Actor, ActorRef, Props}
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadAllData, LoadData, SaveData}
import common.messages.CommonMessages._
import server.actors.messages.AuthToDatabaseMessages.{AddUser, DeleteToken}
import server.actors.messages.PasswordCheckToDatabaseMessages.GetUserCredentials
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken
import server.database.{DatabaseManagement, SysInternalDatabaseManager}

object DatabaseManagementActor {
  def props(sysDbManager: SysInternalDatabaseManager, dbManager: DatabaseManagement, printer: ActorRef): Props
  = Props(new DatabaseManagementActor(sysDbManager: SysInternalDatabaseManager, dbManager: DatabaseManagement, printer: ActorRef))
}

class DatabaseManagementActor(sysDbManager: SysInternalDatabaseManager,
                              dbManager: DatabaseManagement,
                              printer: ActorRef) extends Actor{

  val responses = Responses.DatabaseActorResponses(sysDbManager, dbManager, self)

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
        case SaveData(data, name) =>
          responses.handleSaveData(clientRef, data, name)
        case LoadData(name) =>
          responses.handleLoadData(clientRef, name)
        case DeleteData(name) =>
          responses.handleDeleteData(clientRef, name)
        case LoadAllData =>
          responses.handleLoadAll(clientRef)
      }
    case unexpected: Any =>
      printer ! "[LOG:Db]: Unexpected " + unexpected
  }
}
