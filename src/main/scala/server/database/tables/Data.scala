package server.database.tables
import slick.jdbc.PostgresProfile.api._

class Data(tag: Tag) extends Table[(Int, String, String)](tag, "DATA") {
  def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("NAME")
  def dataString = column[String]("DATA_STRING")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, dataString)
}
