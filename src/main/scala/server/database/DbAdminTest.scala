//package server.database
//
//import scala.util.{Failure, Success}
//
//object DbAdminTest {
//  def testIfAdmin(sysDbManager: SysInternalDatabaseManager, username: String) = {
//    val isAdminFuture = sysDbManager.testIfAdmin(username)
//    isAdminFuture onComplete {
//      case Success(isAdmin) if isAdmin=>
//        println("Admin credentials confirmed")
//        val inactiveUsersFuture = sysDbManager.getInactiveUsers
//        inactiveUsersFuture onComplete {
//          case Success(inactiveUsers) =>
//            clientRef ! InactiveUsers(inactiveUsers)
//          case Failure(result) =>
//            clientRef ! "Failed."
//        }
//      case Failure(res) =>
//        clientRef ! "Failed."
//    }
//  }
//}
