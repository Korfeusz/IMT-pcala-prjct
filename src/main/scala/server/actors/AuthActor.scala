package server.actors
import akka.actor.{Actor, Props}

object AuthActor {
  def props: Props = Props[AuthActor]

}

class AuthActor extends Actor{
  import AuthActor._
  override def receive: Receive = {
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
