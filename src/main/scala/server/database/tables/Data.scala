package server.database.tables
import slick.jdbc.PostgresProfile.api._

class Data(tag: Tag) extends Table[(Int, String, String)](tag, "DATA") {
  def id = column[Int]("SUP_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def dataString = column[String]("DATA_STRING")
  def * = (id, name, dataString)
}
