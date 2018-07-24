package server.actors.messages

object PasswordCheckToDatabaseMessages {
  final case class getUserCredentials(username: String)
  final case class UserCredentials(hash: String, salt: String, activated: Boolean)

}

object  TokenCheckToDatabaseMessage{
  final case class getToken(username: String)
}