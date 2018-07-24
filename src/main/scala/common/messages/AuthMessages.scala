package common.messages


object ClientToAuthMessages {
  final case class RequestRegister(username: String, password: String)
  final case class Login(username: String, password: String)
  final case class Logout(username: String)
}
