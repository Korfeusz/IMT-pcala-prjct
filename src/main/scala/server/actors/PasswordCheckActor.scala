package server.actors

import server.Password.checkPassword
import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import server.actors.messages.PasswordCheckToAuthActorMessages.passwordCheckResult
import server.actors.messages.PasswordCheckToDatabaseMessages._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object PasswordCheckActor {
  def props(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef, clientRef: ActorRef): Props =
    Props(new PasswordCheckActor(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef, clientRef: ActorRef))


}

class PasswordCheckActor(username: String, password: String, databaseActor: ActorRef, parentActor: ActorRef, clientRef: ActorRef) extends Actor{
  import PasswordCheckActor._
  println("Is password checker working?")
  databaseActor ! GetUserCredentials(username)

  override def receive: Receive = {
    case UserCredentials(hash, salt, activated) if activated =>
      println("Checking credentials")
      clientRef ! "Checking credentials."
      parentActor ! passwordCheckResult(username, checkResult = checkPassword(hash, password, salt), clientRef)
      context.system.scheduler.scheduleOnce(1 second, self, PoisonPill)
    case _ => println("got something, but it aint it")
  }

}
