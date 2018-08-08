package server.database

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

object DatabaseManagement {
  def apply(database: Database): DatabaseManagement = new DatabaseManagement(database)
}


class DatabaseManagement(database: Database) {
  import Tables.data

  def saveData(dataString: String, name: String): Future[Unit] = {
    val insertAction = DBIO.seq(data += (0, name, dataString))
    database.run(insertAction)
  }

  def loadData(name: String): Future[String] = {
    val q = for {c <- data if c.name === name} yield c.dataString
    database.run(q.result.head)
  }

  def loadAllNames(): Future[Seq[String]] = {
    val q = for {c <- data } yield c.name
    database.run(q.result)
  }

  def deleteData(name: String): Future[Int] = {
    val q = data.filter(_.name === name)
    database.run(q.delete)
  }

}
