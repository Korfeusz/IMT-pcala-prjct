package server.actors.Responses

import akka.actor.ActorRef
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToDatabaseMessages.{DeleteData, LoadData, SaveData}
import common.messages.CommonMessages._
import server.actors.messages.PasswordCheckToDatabaseMessages.{NoSuchUser, UserCredentials}
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
          clientRef.tell(Response("You do not have the required privileges"), dbActor)
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

  def updateResponeGenerator(numberOfAffected: Int, successMsg: String) = {
    if (numberOfAffected == 1) {
      Response(successMsg)
    }
    else {
      Response("Such a user does not exist.")
    }
  }

  def handleActivateUser(clientRef: ActorRef, senderName: String, userToActivate: String) = {
    val successMsg = "User " + userToActivate + " has been activated."
    val responseGenerator = (num: Any) => updateResponeGenerator(num.asInstanceOf[Int], successMsg)
   testIfAdmin(clientRef, senderName, () => sysDbManager.activateUser(userToActivate), responseGenerator)
  }

  def handleMakeAdmin(clientRef: ActorRef, senderName: String, userToPromote: String) = {
    val successMsg = "User " + userToPromote + " has been promoted to admin."
    val responseGenerator = (num: Any) => updateResponeGenerator(num.asInstanceOf[Int], successMsg)
    testIfAdmin(clientRef, senderName, () => sysDbManager.makeAdmin(userToPromote), responseGenerator)
  }

  def handleDeleteUser(clientRef: ActorRef, senderName: String, userToDelete: String) = {
    val successMsg = "User " + userToDelete + " has been deleted."
    val responseGenerator = (num: Any) => updateResponeGenerator(num.asInstanceOf[Int], successMsg)
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