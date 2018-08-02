package client.actors.messages

object internalClientMessages {
  final case class outgoingMessage(message: Any, recipient: String)
}
