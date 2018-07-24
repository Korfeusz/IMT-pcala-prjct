package server.actors

import akka.actor.{Actor, Props}

object DatabaseManagementActor {
  def props(): Props = Props(new DatabaseManagementActor())
}

class DatabaseManagementActor() extends Actor{
  import DatabaseManagementActor._

  override def receive: Receive = {

    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
