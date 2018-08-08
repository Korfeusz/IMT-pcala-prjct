package server.actors.messages

import akka.actor.ActorRef

object AuthToDatabaseMessages {
  final case class AddUser(username: String, encryptedPass: String, salt: String)
  final case class DeleteToken(username: String)
}

object PasswordCheckToAuthActorMessages {
  final case class passwordCheckResult(username: String, checkResult: Boolean, clientRef: ActorRef)
}

object PasswordCheckToDatabaseMessages {
  final case class GetUserCredentials(username: String)
  final case class UserCredentials(hash: String, salt: String, activated: Boolean)
  case object NoSuchUser

}

object  TokenCheckToDatabaseMessage{
  final case class GetToken(username: String)
}

