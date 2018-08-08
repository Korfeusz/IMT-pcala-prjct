package server.database.tables
import slick.jdbc.PostgresProfile.api._

class Users(tag: Tag) extends Table[(Int, String, String, String, Option[String], Boolean, Boolean)](tag, "USERS") {
  def id = column[Int]("SUP_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("SUP_NAME")
  def passwordHash = column[String]("PASSWORD_HASH")
  def salt = column[String]("SALT")
  def token = column[Option[String]]("TOKEN")
  def isAdmin = column[Boolean]("ADMIN")
  def isAuthorized = column[Boolean]("AUTHORIZED")
  def * = (id, name, passwordHash, salt, token,  isAdmin, isAuthorized)
}
