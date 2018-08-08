package common.messages

object ClientToDatabaseMessages {
  final case class SaveData(data: String, name: String)
  final case class LoadData(name: String)
  final case class DeleteData(name: String)
  case object LoadAllData
  final case class DataNames(names: Seq[String])
  final case class FetchedData(data: String, name: String)
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