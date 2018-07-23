package server.actors
import akka.actor.Actor

class AuthActor extends Actor{

  override def receive: Receive = {
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
