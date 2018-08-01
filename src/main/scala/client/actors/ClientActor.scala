package client.actors

import akka.actor.{Actor, ActorRef, Props}
import common.messages.ClientToAuthMessages._
import common.messages.CommonMessages._


import scala.util.Random

object ClientActor {
  def props(): Props = Props(new ClientActor())
}

class ClientActor extends Actor{
  import ClientActor._

  override def receive: Receive = {
    case unexpected: Any =>
      println("Response: Unexpected " + unexpected)
  }
}
