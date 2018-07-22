package server.actors.messages

object ServerToCLIMessages {
  case object RunCLI
  case object StatusOfCLI
  case object ShutdownRequest
}

object CLIToAuthMessages {
  case object StatusOfAuth
  final case class GiveAdminPrivileges(username : String)
  case object StopAuth
}

object CLIToDeviceManagerMessages {
  case object RunDeviceManager
  case object StatusOfDeviceManager
  case object StopDeviceManager
}

object AuthToDeviceManagerMessages {
  final case class Token(username: String, tokenString: String)
  final case class UpdateUser(username: String) // Pass update class
  final case class SearchForUser(username: String)
  final case class SaveUser(username: String, hashedPassword: String, salt: String)
  final case class DeleteUser(username: String)
  final case class GiveAdminPrivileges(username: String)
  final case class UserCredentials(username: String, hashedPassword: String, salt: String)
  final case class GetUserCredentials(username: String)
  final case class Logout(username: String)
}
