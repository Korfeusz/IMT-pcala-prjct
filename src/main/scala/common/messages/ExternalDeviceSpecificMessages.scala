package common.messages

object ClientToDatabaseMessages {
  final case class SaveData(data: Any, where: String)
  final case class LoadData(where: String)
  final case class DeleteData(where: String)
}

object AdminToDatabaseMessages {
  case object GetInactiveUsers
  case object GetAllUsers
  final case class InactiveUsers(users: collection.Seq[String])
  final case class AllUsers(users: collection.Seq[String])
  final case class ActivateUser(username:String)
  final case class MakeAdmin(username: String)
  final case class DeleteUser(username: String)
}