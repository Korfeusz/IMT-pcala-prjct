package server.actors
import akka.actor.Actor

class AuthActor extends Actor{
  import messages.Messages._

  override def receive: Receive = {
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
