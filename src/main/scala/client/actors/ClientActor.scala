package client.actors

import akka.actor.{Actor, ActorRef, Props}
import client.actors.messages.internalClientMessages.outgoingMessage
import common.messages.ClientToAuthMessages._
import common.messages.CommonMessages._

import scala.util.Random

object ClientActor {
  def props(printerActorRef: ActorRef): Props =
    Props(new ClientActor(printerActorRef: ActorRef))
}

class ClientActor(printerActorRef: ActorRef) extends Actor{
  import ClientActor._

  override def receive: Receive = {
    case outgoingMessage(message, recipient) =>
      context.actorSelection(recipient) ! ActorRefWrap(self, message)
    case Response(text) =>
      printerActorRef ! text
    case text: String =>
      printerActorRef ! text
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
