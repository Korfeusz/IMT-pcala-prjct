package server
import scala.util.Random

object Password {
  def apply(passwordString: String): Password = new Password(passwordString)

  def checkPassword(correctHash: String, triedPasswordString: String, salt: String) : Boolean = {
    correctHash == Password(triedPasswordString).saltPassword(salt).hashPassword.toString
  }

  def generateNewHashAndSalt(textPassword: String): Map[String, String] = {
    val salt : String = (Random.alphanumeric take 16).mkString
    Map("hash" -> Password(textPassword).saltPassword(salt).hashPassword.toString,
        "salt" -> salt)
  }
}

class Password(passwordString: String) {
  import Password._
  def hashPassword : Password = {
    new Password(
    String.format("%064x", new java.math.BigInteger(1,
      java.security.MessageDigest.getInstance("SHA-256")
        .digest(passwordString.getBytes("UTF-8"))))
    )
  }

  def saltPassword(salt: String): Password = {
    new Password(salt + passwordString)
  }

  override def toString:String = passwordString
}
