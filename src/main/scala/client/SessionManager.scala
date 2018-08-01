package client
import akka.actor.ActorRef
import common.messages.CommonMessages.LogText

object SessionManager {
  def apply(clientActorRef: ActorRef, printerActorRef: ActorRef): SessionManager =
    new SessionManager(clientActorRef, printerActorRef: ActorRef)
}

class SessionManager(clientActorRef: ActorRef, printerActorRef: ActorRef) {
  def readUsernameAndPassword(): (String, String) = {
    var username: Array[String] = Array()
    var password: Array[String] = Array()
    do {
      username = scala.io.StdIn.readLine("Uusername: ").split(" ")
      password = scala.io.StdIn.readLine("Password: ").split(" ")
    } while(username.length != 1 | password.length != 1)
    (username(0), password(0))
  }

  def register() = {
    val (username, password) = readUsernameAndPassword()
    printerActorRef.tell(LogText(username + " " + password), clientActorRef)
  }

}
