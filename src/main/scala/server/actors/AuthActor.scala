package server.actors
import akka.actor.{Actor, ActorRef, Props}
import common.messages.ClientToAuthMessages._
import common.messages.CommonMessages._
import server.actors.messages.AuthToDatabaseMessages._
import server.Password.{checkPassword, generateNewHashAndSalt}
import server.actors.messages.PasswordCheckToAuthActorMessages.passwordCheckResult
import server.actors.PasswordCheckActor

import scala.util.Random

object AuthActor {
  def props(databaseActor: ActorRef): Props = Props(new AuthActor(databaseActor: ActorRef))
}

class AuthActor(databaseActor: ActorRef) extends Actor{
  import AuthActor._

  override def receive: Receive = {
    case RequestRegister(username, password) =>
      val hashNSalt = generateNewHashAndSalt(password)
      databaseActor ! AddUser(username, hashNSalt("hash"), hashNSalt("salt"))
      sender ! Response("Account registration request created, please be patient.")
    case Login(username, password) =>
      context.actorOf(PasswordCheckActor.props(username, password, databaseActor, self))
    case passwordCheckResult(username, checkResult) =>
      if(checkResult) {
        val tokenString = (Random.alphanumeric take 16).mkString
        databaseActor ! Token(username, tokenString)

        // TODO send success message and token to client
      } else {
        // TODO send fail message
      }
    case TokenWrap(message, token) =>
      context.actorOf(TokenCheckActor.props(token, message, databaseActor, self))
    case TokenCheckResult(_, message, result) if result =>
      message match {
        case Logout(username) =>
          databaseActor ! DeleteToken(username)
      }
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
