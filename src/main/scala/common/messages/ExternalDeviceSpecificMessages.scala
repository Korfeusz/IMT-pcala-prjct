package common.messages

object ClientToDatabaseMessages {
  final case class SaveData(data: Any, where: String)
  final case class LoadData(where: String)
  final case class DeleteData(where: String)
}

object AdminToDatabaseMessages {
  case object GetInactiveUsers
  final case class InactiveUsers(users: collection.Set[String])
  final case class ActivateUser(username:String)
}