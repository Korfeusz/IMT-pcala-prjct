package server.actors.messages

object AuthToDatabaseMessages {
  final case class AddUser(username: String, encryptedPass: String, salt: String)
  final case class DeleteToken(username: String)
}

object PasswordCheckToAuthActorMessages {
  final case class passwordCheckResult(username: String, checkResult: Boolean)
}

object PasswordCheckToDatabaseMessages {
  final case class getUserCredentials(username: String)
  final case class UserCredentials(hash: String, salt: String, activated: Boolean)

}

object  TokenCheckToDatabaseMessage{
  final case class getToken(username: String)
}

