package server.actors.messages

object AuthToDatabaseMessages {
  final case class AddUser(username: String, encryptedPass: String, salt: String)
  final case class UserStatusRequest(username: String)
  final case class UserStatus(username: String, admin: Boolean, active: Boolean)
  final case class DeleteToken(username: String)
}

object PasswordCheckToAuthActorMessages {
  final case class passwordCheckResult(username: String, checkResult: Boolean)
}
