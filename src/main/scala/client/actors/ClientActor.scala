package client.actors

import akka.actor.{Actor, ActorRef, Props}
import client.actors.messages.internalClientMessages.{outgoingMessage, sessionStartMessage}
import common.messages.AdminToDatabaseMessages.{AllUsers, InactiveUsers}
import common.messages.ClientToDatabaseMessages.{DataNames, FetchedData}
import common.messages.CommonMessages._

object ClientActor {
  def props(printerActorRef: ActorRef): Props =
    Props(new ClientActor(printerActorRef: ActorRef))
}

class ClientActor(printerActorRef: ActorRef) extends Actor{
  var tokenString: String = ""
  var name: String = ""

  override def receive: Receive = {
    case Token(username, tokenStr) =>
      tokenStr match {
        case Some(tokenStr) => tokenString = tokenStr
        case None => printerActorRef ! Response("This user is not logged in.")
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
    case DataNames(names) =>
      printerActorRef ! "All data names"
      printerActorRef ! names
    case FetchedData(data, name) =>
      printerActorRef ! "Found data:\n"
      printerActorRef ! "name: " + name + "\n"
      printerActorRef ! Response("Data: " + data)
    case Response(text) =>
      printerActorRef ! Response(text)
    case text: String =>
      printerActorRef ! text
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
