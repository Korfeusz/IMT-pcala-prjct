package server.database

object DatabaseManagement {
  def saveData(data: Any, where: String) : Unit = println("Saving to: " + where)
  def loadData(where: String) : Unit = println("Loading from: " + where)
  def deleteData(where: String) : Unit = println("Deleting from: " + where)
}
