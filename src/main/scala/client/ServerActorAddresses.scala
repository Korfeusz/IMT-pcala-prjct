package client

import akka.actor.Address

object ServerActorAddresses {
  def apply(host: String, port: Int): ServerActorAddresses = new ServerActorAddresses(host: String, port: Int)
}

class ServerActorAddresses(host: String, port: Int) {
  val serverAddress: String = Address("akka.tcp", "ServerSystem", host, port).toString + "/user/"
  val databaseAddress: String = serverAddress + "DbActor"
  val authAddress: String = serverAddress + "AuthActor"
}