package client
import akka.actor.ActorRef
import client.ClientDriver.system
import client.actors.ClientActor
import client.actors.messages.internalClientMessages.{outgoingMessage, sessionStartMessage}
import common.messages.AdminToDatabaseMessages._
import common.messages.ClientToAuthMessages.Logout
import common.messages.CommonMessages.Response

object CommandLineInterface {
  def apply(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses): CommandLineInterface =
    new CommandLineInterface(clientActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses)
}

class CommandLineInterface(clientActorRef: ActorRef, printerActorRef: ActorRef, serverAddresses: ServerActorAddresses) {
  val sessionManager: SessionManager = SessionManager(clientActorRef, printerActorRef, serverAddresses: ServerActorAddresses)
  var username: String = ""

  def printGreetingMessage(): Unit = {
    printerActorRef ! Response("Welcome! \n")
  }

  def startInputLoop(): Unit = {
    printGreetingMessage()
    while(true) {
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
        clientActorRef ! outgoingMessage(Logout(username), serverAddresses.authAddress)
      case "stop" | "quit" | "q" | "exit" =>
        sys.exit()
      case _ => printerActorRef ! Response("Couldn't find command: " + input)
    }
    case Seq(_, _) => printerActorRef ! Response("Command not found.")

    case Seq(command, operation, target) => command match {
      case "users" => operation match {
        case "get" => target match {
          case "unactivated" =>
            clientActorRef ! outgoingMessage(GetInactiveUsers, serverAddresses.databaseAddress)
          case "all" =>
            clientActorRef ! outgoingMessage(GetAllUsers, serverAddresses.databaseAddress)
          case _ => printerActorRef ! Response("Parameter: " + target + " doesnt't match anything to get.")
        }
        case "activate" => clientActorRef ! outgoingMessage(ActivateUser(target), serverAddresses.databaseAddress)
        case "admin" => clientActorRef ! outgoingMessage(MakeAdmin(target), serverAddresses.databaseAddress)
        case "delete" => clientActorRef ! outgoingMessage(DeleteUser(target), serverAddresses.databaseAddress)
        case _ => printerActorRef ! Response("Command not found.")
      }
      case "database" => operation match {
        case "get" => target match {
          case "names" => println("fetching")
          case name: String => println("fetching specific data")
        }
        case "delete" => println("Deleting data")
      }
      case _ => printerActorRef ! Response("Command not found.")
    }
    case Seq(command, operation, target, parameter) => command match {
      case "database" => operation match {
        case "update" => println("updating")
        case "add" => println("adding")
        case _ => printerActorRef ! Response("Command not found.")
      }
      case _ => printerActorRef ! Response("Command not found.")
    }
    case _ => printerActorRef ! Response("Command not found.")
  }
}