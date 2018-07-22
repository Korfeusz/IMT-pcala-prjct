package common.messages

object ClientToAuthMessages {
  final case class Login(username: String, password: String)
  final case class Logout(username: String, tokenString: String)

}
