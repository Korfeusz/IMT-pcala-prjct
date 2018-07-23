package server.actors.messages

object DeviceManagerToDatabaseMessages{
  // General Messages
  case object StartDatabase
  case object StopDatabase
  // Authentication and Authorisation Messages
  import server.actors.messages.AuthToDeviceManagerMessages._
  // User querying Messages
  import common.messages.ClientToDatabaseMessages._
}

