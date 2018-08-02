package common.messages

import akka.actor.ActorRef

object CommonMessages {
  final case class ActorRefWrap(client: ActorRef, message: Any)
  final case class Response(message: String)
  final case class LogText(text: String)
  final case class Token(username: String, tokenString: String)
  final case class TokenWrap(message: Any, token: Token)
  final case class TokenCheckResult(username: String, originalMessage: Any, checkResult: Boolean)
}
