package server.actors

import server.Password.checkPassword
import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import server.actors.messages.PasswordCheckToAuthActorMessages.passwordCheckResult
import server.actors.messages.PasswordCheckToDatabaseMessages._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object PasswordCheckActor {
  def props(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef): Props =
    Props(new PasswordCheckActor(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef))


}

class PasswordCheckActor(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef) extends Actor{
  import PasswordCheckActor._
  databaseActor ! GetUserCredentials(username)

  override def receive: Receive = {
    case UserCredentials(hash, salt, activated) if activated =>
      parentActor ! passwordCheckResult(username, checkResult = checkPassword(hash, password, salt))
      context.system.scheduler.scheduleOnce(1 second, self, PoisonPill)
  }

}
