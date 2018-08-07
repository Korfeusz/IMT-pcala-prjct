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

  override def receive: Receive = {
    case AddUser(username, encryptedPass, salt) =>
      sysDbManager.addUser(username, encryptedPass, salt)
    case DeleteToken(username) =>
      sysDbManager.deleteToken(username)
    case Token(username, tokenString) =>
      sysDbManager.addToken(username, tokenString)
    case GetUserCredentials(username) =>
      val credFuture = sysDbManager.getUserCredentials(username)
      val originalSender = sender
      credFuture onComplete {
        case Success(result) =>
          val (hash, salt, isAuthorized) = result
          println(isAuthorized)
          originalSender ! UserCredentials(hash, salt, isAuthorized)
        case Failure(result) =>
          sender ! "Failed."
      }
    case GetToken(username) =>
      println("dbman getToken received")
      val tokenFuture = sysDbManager.getToken(username)
      val originalSender = sender
      tokenFuture onComplete {
        case Success(result) =>
          println("dbman token ready")
          originalSender ! Token(username, result)
        case Failure(cause) =>
          println("failed to get token " + cause)
          originalSender ! "Failed."
      }
    case ActorRefWrap(clientRef, message) => message match {
      case TokenWrap(message, token) =>
        context.actorOf(TokenCheckActor.props(token, message, self, self, clientRef))
    }
    case TokenCheckResult(senderName, message, result, clientRef) if result =>
      message match {
        case GetInactiveUsers  =>
          val isAdminFuture = sysDbManager.testIfAdmin(senderName)
          isAdminFuture onComplete {
            case Success(isAdmin) if isAdmin=>
              println("Admin credentials confirmed")
              val inactiveUsersFuture = sysDbManager.getInactiveUsers
              inactiveUsersFuture onComplete {
                case Success(inactiveUsers) =>
                  clientRef ! InactiveUsers(inactiveUsers)
                case Failure(result) =>
                  clientRef ! "Failed."
              }
            case Failure(res) =>
              clientRef ! "Failed."
          }
        case GetAllUsers =>
          val isAdminFuture = sysDbManager.testIfAdmin(senderName)
          isAdminFuture onComplete {
            case Success(isAdmin) if isAdmin=>
              println("Admin credentials confirmed")
              val AllUsersFuture = sysDbManager.getAllUsers
              AllUsersFuture onComplete {
                case Success(allUsers) =>
                  clientRef ! AllUsers(allUsers)
                case Failure(result) =>
                  clientRef ! "Failed."
              }
            case Failure(res) =>
              clientRef ! "Failed."
          }
        case ActivateUser(username) =>
          val isAdminFuture = sysDbManager.testIfAdmin(senderName)
          isAdminFuture onComplete {
            case Success(isAdmin) if isAdmin=>
              println("Admin credentials confirmed")
              sysDbManager.activateUser(username)
              clientRef ! Response("User " + username + " has been activated.")
            case Failure(res) =>
              clientRef ! "Failed."
          }
        case MakeAdmin(username) =>
          val isAdminFuture = sysDbManager.testIfAdmin(senderName)
          isAdminFuture onComplete {
            case Success(isAdmin) if isAdmin=>
              println("Admin credentials confirmed")
              sysDbManager.makeAdmin(username)
              clientRef ! Response("User " + username + " has been made an admin")
            case Failure(res) =>
              clientRef ! "Failed."
          }
        case DeleteUser(username) =>
          val isAdminFuture = sysDbManager.testIfAdmin(senderName)
          isAdminFuture onComplete {
            case Success(isAdmin) if isAdmin=>
              println("Admin credentials confirmed")
              sysDbManager.deleteUser(username)
              clientRef ! Response("User " + username + " has been deleted")
            case Failure(res) =>
              clientRef ! "Failed."
          }
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
