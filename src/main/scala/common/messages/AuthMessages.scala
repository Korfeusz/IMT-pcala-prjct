package common.messages

object ClientToAuthMessages {
  final case class RequestCreateAccount(username: String, password: String)
  final case class Login(username: String, password: String)
  final case class Logout(username: String, tokenString: String)
  final case class Token(tokenString: String)
  final case class RequestAdminPrivileges(username: String)
  final case class RequestAuthenticateNewUser(username: String)
}
