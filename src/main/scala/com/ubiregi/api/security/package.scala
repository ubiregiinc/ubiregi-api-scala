package com.ubiregi.api

import java.security.SecureRandom
import java.security.MessageDigest

package object security {
  private final val SEPARATOR = ":"
  def encrypt(secret: String): (String, String) = {
    def generateSalt(): String = {
      val random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.currentTimeMillis())
      "%08x".format(random.nextInt())
    }
    val salt = generateSalt()
    val digester = MessageDigest.getInstance("SHA")
    digester.update((salt + SEPARATOR + secret).getBytes())
    val base64Encoded = new BASE64Encoder().encode(digester.digest())
    (base64Encoded, salt)
  }
}