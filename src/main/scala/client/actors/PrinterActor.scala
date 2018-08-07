package client.actors

import akka.actor.{Actor, ActorLogging, Props}
import common.messages.CommonMessages.LogText

object PrinterActor {
  def props: Props = Props[PrinterActor]
}

class PrinterActor extends Actor with ActorLogging {
  import PrinterActor._

  def receive = {
    case sequence: collection.Seq[String] =>
      sequence.foreach(println)
    case LogText(text) =>
      log.info("[Sender: " + sender + "], msg: " + text)
    case  text: String =>
      println(text)

  }
}