//package client
//
//import akka.actor.{Actor, Props}
//
//object CommandLineInterfaceActor {
//  def props: Props = Props[CommandLineInterfaceActor]
//  def CLILoop() ={}
//  def getStatusOfAuth() = {}
//  def giveAdminPrivileges(username : String) = {}
//  def stopAuth() = {}
//  def runDeviceManager() = {}
//  def getStatusOfDeviceManager() = {}
//  def stopDeviceManager() = {}
//}
//
//class CommandLineInterfaceActor extends Actor {
//  import common.messages.CommonMessages._
//  import server.actors.messages.ServerToCLIMessages._
//
//  def receive = {
//    case RunCLI =>
//      println("RunCli")
//    case StatusOfCLI =>
//      println("statusOfCLI")
//    case Response(message) =>
//      println("Response " + message)
//    case unexpected: Any =>
//      println("None" + unexpected)
//  }
//}
//
//
//
//  case object StatusOfAuth
//  final case class GiveAdminPrivileges(username : String)
//  case object StopAuth
//
//  case object RunDeviceManager
//  case object StatusOfDeviceManager
//  case object StopDeviceManager
