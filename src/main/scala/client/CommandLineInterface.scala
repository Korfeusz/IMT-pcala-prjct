package client
import akka.actor.ActorRef

object CommandLineInterface {
  def apply(clientActorRef: ActorRef, printerActorRef: ActorRef): CommandLineInterface =
    new CommandLineInterface(clientActorRef, printerActorRef: ActorRef)
}

class CommandLineInterface(clientActorRef: ActorRef, printerActorRef: ActorRef) {
  val sessionManager: SessionManager = SessionManager(clientActorRef, printerActorRef)

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
        sessionManager.register()
      case "login" =>
        println("login")
      case "logout" =>
        println("logout")
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
      case _ => println("WRONG!")
    }

  }
}