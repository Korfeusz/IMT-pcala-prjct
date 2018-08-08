package client
import akka.actor.ActorRef
import client.actors.messages.internalClientMessages.sessionStartMessage
import common.messages.ClientToAuthMessages.{Login, RequestRegister}

object SessionManager {
  def apply(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses): SessionManager =
    new SessionManager(clientActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses)
}

class SessionManager(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses) {
  def readUsernameAndPassword(): (String, String) = {
    var username: Array[String] = Array()
    var password: Array[String] = Array()
    do {
      username = scala.io.StdIn.readLine("Username: ").split(" ")
      password = scala.io.StdIn.readLine("Password: ").split(" ")
    } while(username.length != 1 | password.length != 1)
    (username(0), password(0))
  }

  def register(): String = {
    val (username, password) = readUsernameAndPassword()
    clientActorRef ! sessionStartMessage(RequestRegister(username, password), serverAddresses.authAddress)
    return username
  }

  def login(): String = {
    val (username, password) = readUsernameAndPassword()
    clientActorRef ! sessionStartMessage(Login(username, password), serverAddresses.authAddress)
    return username
  }
}
