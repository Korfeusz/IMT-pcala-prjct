package client
import akka.actor.ActorRef
import client.ClientDriver.system
import client.actors.ClientActor
import client.actors.messages.internalClientMessages.{outgoingMessage, sessionStartMessage}
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToAuthMessages.Logout

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
        printerActorRef ! "Logout request created, goodbye \n"
        clientActorRef ! outgoingMessage(Logout(username), serverAddresses.authAddress)
      case "stop" | "quit" | "q" | "exit" =>
        sys.exit()
      case _ =>
        println(input)
    }
    case Seq(command, parameter) => command match {
      case "get" =>
        parameter match {
          case "unactivated" =>
            clientActorRef ! outgoingMessage(GetInactiveUsers, serverAddresses.databaseAddress)
          case "all" =>
            clientActorRef ! outgoingMessage(GetAllUsers, serverAddresses.databaseAddress)
        }
      case "activate" => clientActorRef ! outgoingMessage(ActivateUser(parameter), serverAddresses.databaseAddress)
      case "admin" => clientActorRef ! outgoingMessage(MakeAdmin(parameter), serverAddresses.databaseAddress)
      case "delete" => clientActorRef ! outgoingMessage(DeleteUser(parameter), serverAddresses.databaseAddress)
      case "text" => clientActorRef ! sessionStartMessage(parameter, serverAddresses.authAddress)
      case _ => println("WRONG!")
    }

  }
}