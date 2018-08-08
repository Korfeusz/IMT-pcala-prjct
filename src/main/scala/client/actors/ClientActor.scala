package client.actors

import akka.actor.{Actor, ActorRef, Props}
import client.actors.messages.internalClientMessages.{outgoingMessage, sessionStartMessage, setUsername}
import common.messages.AdminToDatabaseMessages.{AllUsers, InactiveUsers}
import common.messages.ClientToAuthMessages._
import common.messages.CommonMessages._

import scala.util.Random

object ClientActor {
  def props(printerActorRef: ActorRef): Props =
    Props(new ClientActor(printerActorRef: ActorRef))
}

class ClientActor(printerActorRef: ActorRef) extends Actor{
  import ClientActor._
  var tokenString: String = ""
  var name: String = ""

  override def receive: Receive = {
//    case setUsername(username) => name = username
    case Token(username, tokenStr) =>
      tokenStr match {
        case Some(tokenStr) => tokenString = tokenStr
        case None => printerActorRef ! "This user is not logged in."
      }
      name = username
    case outgoingMessage(message, recipient) =>
      context.actorSelection(recipient) ! ActorRefWrap(self, TokenWrap(message, Token(name, Option(tokenString))))
    case sessionStartMessage(message, recipient) =>
      context.actorSelection(recipient) ! ActorRefWrap(self, message)
    case InactiveUsers(users) =>
      printerActorRef ! "Unauthorized users:"
      printerActorRef ! users
    case AllUsers(users) =>
      printerActorRef ! "All users:"
      printerActorRef ! users
    case Response(text) =>
      printerActorRef ! text
    case text: String =>
      printerActorRef ! text
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
