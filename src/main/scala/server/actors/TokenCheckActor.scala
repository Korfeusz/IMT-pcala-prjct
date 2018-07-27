package server.actors

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import common.messages.CommonMessages.{Token, TokenCheckResult}
import server.actors.messages.TokenCheckToDatabaseMessage.GetToken
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object TokenCheckActor {
  def props(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef): Props =
    Props(new TokenCheckActor(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef))


}

class TokenCheckActor(triedToken: Token, originalMessage: Any, databaseActor: ActorRef, parentActor: ActorRef) extends Actor{
  import TokenCheckActor._
  databaseActor ! GetToken(triedToken.username)

  override def receive: Receive = {
    case Token(_, tokenString) =>
        parentActor ! TokenCheckResult(triedToken.username, originalMessage, checkResult = {tokenString == triedToken.tokenString})
      context.system.scheduler.scheduleOnce(1 second, self, PoisonPill)
  }

}
