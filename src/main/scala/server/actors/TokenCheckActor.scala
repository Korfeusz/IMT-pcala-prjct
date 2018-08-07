package server.actors

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import client.actors.ClientActor
import common.messages.CommonMessages.{Token, TokenCheckResult}
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object TokenCheckActor {
  def props(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef, clientActor: ActorRef): Props =
    Props(new TokenCheckActor(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef, clientActor: ActorRef))


}

class TokenCheckActor(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef, clientActor: ActorRef) extends Actor{
  import TokenCheckActor._
  databaseActor ! GetToken(triedToken.username)

  override def receive: Receive = {
    case Token(_, tokenString) =>
        parentActor ! TokenCheckResult(triedToken.username, originalMessage, checkResult = {tokenString == triedToken.tokenString}, clientActor)
      context.system.scheduler.scheduleOnce(1 second, self, PoisonPill)
  }

}
