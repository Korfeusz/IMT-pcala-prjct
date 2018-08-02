package client
import akka.actor.ActorRef
import client.ClientDriver.system
import client.actors.ClientActor
import client.actors.messages.internalClientMessages.outgoingMessage

object CommandLineInterface {
  def apply(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses): CommandLineInterface =
    new CommandLineInterface(clientActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses)
}

class CommandLineInterface(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses) {
  val sessionManager: SessionManager = SessionManager(clientActorRef, printerActorRef, serverAddresses: ServerActorAddresses)
  var username: String = ""

  def printGreetingMessage(): Unit = {
    printerActorRef ! "Welcome! \n"
  }

  def startInputLoop(): Unit = {
    printGreetingMessage()
    while(true) {
      printerActorRef ! "> "
      parseInput(scala.io.StdIn.readLine().toLowerCase.split(" "))
    }
  }

  def parseInput(input: Seq[String]): Unit = input match {
    case Seq(command) => command match {
      case "register" =>
        username = sessionManager.register()
      case "login" =>
        username = sessionManager.login()
      case "logout" =>
        sessionManager.logout(username)
      case "stop" | "quit" | "q" | "exit" =>
        sys.exit()
      case _ =>
        println(input)
    }
    case Seq(command, parameter) => command match {
      case "get" if parameter == "unactivated" =>
        println("get unactivated users")
      case "activate" => println("activate: " + parameter)
      case "admin" => println("make admin: " + parameter)
      case "text" => clientActorRef ! outgoingMessage(parameter, serverAddresses.databaseAddress)
      case _ => println("WRONG!")
    }

  }
}