package server.actors.Responses

import akka.actor.{ActorLogging, ActorRef}
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadData, SaveData}
import common.messages.CommonMessages._
import server.actors.TokenCheckActor
import server.actors.messages.AuthToDatabaseMessages.{AddUser, DeleteToken}
import server.actors.messages.PasswordCheckToDatabaseMessages.{GetUserCredentials, NoSuchUser, UserCredentials}
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken
import server.database.{DatabaseManagement, SysInternalDatabaseManager}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object DatabaseActorResponses {
  def apply(sysDbManager: SysInternalDatabaseManager, DbActor: ActorRef): DatabaseActorResponses
  = new DatabaseActorResponses(sysDbManager, DbActor)
}

class DatabaseActorResponses(sysDbManager: SysInternalDatabaseManager, dbActor: ActorRef){
  def handleGetUserCredentials(username: String, sender: ActorRef): Unit = {
    sysDbManager.getUserCredentials(username) onComplete {
      case Success(credentials) =>
        val (hash, salt, isAuthorized) = credentials
        sender.tell(UserCredentials(hash, salt, isAuthorized), dbActor)
      case Failure(_) => sender.tell(NoSuchUser, dbActor)
    }
  }

  def handleGetToken(username: String, sender: ActorRef): Unit = {
    sysDbManager.getToken(username) onComplete {
      case Success(tokenString) =>
        sender.tell(Token(username, tokenString), dbActor)
      case Failure(e) => sender.tell(NoSuchUser, dbActor)
    }
  }

  def testIfAdmin(clientRef: ActorRef, username: String, guardedTask: () => Future[Any], responseGenerator: (Any) => Any) = {
    val bulkFuture = for {
      isAdmin <- sysDbManager.testIfAdmin(username)
      qResult <- if (isAdmin) guardedTask() else Future.successful(Seq.empty) // Here
    } yield (qResult, isAdmin)

    bulkFuture onComplete {
      case Success(result) =>
        val (qResult, isAdmin) = result
        if (isAdmin) {
          clientRef.tell(responseGenerator(qResult), dbActor) // Here
        } else {
          clientRef.tell("You do not have the required priviliges", dbActor)
        }
      case Failure(e) =>
        e.printStackTrace()
    }
  }


  def handleGetInactiveUsers(clientRef: ActorRef, username: String) = {
    val responseGenerator = (inactiveUsers: Any) => InactiveUsers(inactiveUsers.asInstanceOf[Seq[String]])
    testIfAdmin(clientRef, username, sysDbManager.getInactiveUsers, responseGenerator)
  }

  def handleGetAllUsers(clientRef: ActorRef, username: String) = {
    val responseGenerator = (users: Any) => AllUsers(users.asInstanceOf[Seq[String]])
    testIfAdmin(clientRef, username, sysDbManager.getAllUsers, responseGenerator)
  }

  def handleActivateUser(clientRef: ActorRef, senderName: String, userToActivate: String) = {
    val responseGenerator = (_: Any) => Response("User " + userToActivate + " has been activated.")
   testIfAdmin(clientRef, senderName, () => sysDbManager.activateUser(userToActivate), responseGenerator)
  }

  def handleMakeAdmin(clientRef: ActorRef, senderName: String, userToPromote: String) = {
    val responseGenerator = (_: Any) => Response("User " + userToPromote + " has been promoted to admin.")
    testIfAdmin(clientRef, senderName, () => sysDbManager.makeAdmin(userToPromote), responseGenerator)
  }

  def handleDeleteUser(clientRef: ActorRef, senderName: String, userToDelete: String) = {
    val responseGenerator = (_: Any) => Response("User " + userToDelete + " has been deleted.")
    testIfAdmin(clientRef, senderName, () => sysDbManager.deleteUser(userToDelete), responseGenerator)
  }


}

//case SaveData(data, where) =>
//DatabaseManagement.saveData(data, where)
//case LoadData(where) =>
//DatabaseManagement.loadData(where)
//case DeleteData(where) =>
//DatabaseManagement.deleteData(where)
//}
//case actor: ActorRef =>
//actor ! "I got it!"
//case unexpected: Any =>
//println("Response: Unexpected " + unexpected)