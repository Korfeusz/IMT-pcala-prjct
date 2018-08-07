package client.actors.messages

object internalClientMessages {
  final case class sessionStartMessage(message: Any, recipient: String)
  final case class outgoingMessage(message: Any, recipient: String)
  final case class setUsername(username: String)
}
