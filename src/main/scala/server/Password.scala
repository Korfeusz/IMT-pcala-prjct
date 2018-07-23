package server

object Password {
  def apply(passwordString: String): Password = new Password(passwordString)

  def checkPassword(correctHash: String, triedPasswordString: String, salt: String) : Boolean = {
    correctHash == Password(triedPasswordString).saltPassword(salt).hashPassword().toString
  }
}

class Password(passwordString: String) {
  def hashPassword() : Password = {
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
