package client


object StartupManager {
  def readUsernameAndPassword(): (String, String) = {
    var username: Array[String] = Array()
    var password: Array[String] = Array()
    do {
      username = scala.io.StdIn.readLine("Uusername: ").split(" ")
      password = scala.io.StdIn.readLine("Password: ").split(" ")
    } while(username.length != 1 | password.length != 1)
    (username(0), password(0))
  }

  def run(): (String, String) = {
    println("Welcome, to start please provide credentials: \n")
    readUsernameAndPassword()
  }
}
