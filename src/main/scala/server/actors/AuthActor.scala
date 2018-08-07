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

    case ActorRefWrap(clientRef, message) =>
      message match {
        case Login(username, password) =>
          sender ! "Login request, test"
          context.actorOf(PasswordCheckActor.props(username, password, databaseActor, self, clientRef))

        case RequestRegister(username, password) =>
          val hashNSalt = generateNewHashAndSalt(password)
          databaseActor ! AddUser(username, hashNSalt("hash"), hashNSalt("salt"))
          sender ! Response("Account registration request created, please be patient.")
        case TokenWrap(message, token) =>
          context.actorOf(TokenCheckActor.props(token, message, databaseActor, self, clientRef))
      }
    case passwordCheckResult(username, checkResult, clientRef) =>
      if(checkResult) {
        val tokenString = (Random.alphanumeric take 16).mkString
        databaseActor ! Token(username, tokenString)
        clientRef ! Token(username, tokenString)
        clientRef ! "Login Successful"
      } else {
        clientRef ! "Something went wrong, please try again."
      }
    case TokenCheckResult(_, message, result, clientRef) if result =>
      message match {
        case Logout(username) =>
          databaseActor ! DeleteToken(username)
          clientRef ! "Logout succesful"
      }
    case actor : ActorRef =>
      databaseActor ! actor
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
