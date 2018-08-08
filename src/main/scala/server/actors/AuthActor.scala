package server.actors
import akka.actor.{Actor, ActorRef, Props}
import common.messages.ClientToAuthMessages._
import common.messages.CommonMessages._
import server.Password.generateNewHashAndSalt
import server.actors.messages.AuthToDatabaseMessages._
import server.actors.messages.PasswordCheckToAuthActorMessages.passwordCheckResult

import scala.util.Random

object AuthActor {
  def props(databaseActor: ActorRef): Props = Props(new AuthActor(databaseActor: ActorRef))
}

class AuthActor(databaseActor: ActorRef) extends Actor{

  override def receive: Receive = {

    case ActorRefWrap(clientRef, message) =>
      message match {
        case Login(username, password) =>
          context.actorOf(PasswordCheckActor.props(username, password, databaseActor, self, clientRef))
        case RequestRegister(username, password) =>
          val hashNSalt = generateNewHashAndSalt(password)
          databaseActor ! AddUser(username, hashNSalt("hash"), hashNSalt("salt"))
          sender ! Response("Account registration request created. \nPlease wait to be authorized by an admin.")
        case TokenWrap(message, token) =>
          context.actorOf(TokenCheckActor.props(token, message, databaseActor, self, clientRef))
      }
    case passwordCheckResult(username, checkResult, clientRef) =>
      if(checkResult) {
        val tokenString = (Random.alphanumeric take 16).mkString
        databaseActor ! Token(username, Some(tokenString))
        clientRef ! Token(username, Some(tokenString))
        clientRef ! Response("Login Successful")
      } else {
        clientRef ! Response("Wrong username or password.")
      }
    case TokenCheckResult(_, message, result, clientRef) if result =>
      message match {
        case Logout(username) =>
          databaseActor ! DeleteToken(username)
          clientRef ! Response("Logout successful")
      }
    case actor : ActorRef =>
      databaseActor ! actor
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
