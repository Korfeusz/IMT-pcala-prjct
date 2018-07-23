package common.messages

object ClientToDatabaseMessages {
  final case class QueryDatabase(query: String) // Some query type
  final case class DatabaseResponse(response: String) //Response type
}
