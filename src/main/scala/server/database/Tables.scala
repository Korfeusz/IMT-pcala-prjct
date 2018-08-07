package server.database

import server.database.tables.{Data, Users}
import slick.lifted.TableQuery

object Tables {
  val users = TableQuery[Users]
  val data = TableQuery[Data]
}
