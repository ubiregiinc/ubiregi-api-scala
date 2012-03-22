package com.ubiregi.api

import java.security.SecureRandom
import java.security.MessageDigest

package object security {
  private final val SEPARATOR = ":"
  def encrypt(secret: String): String = {
    def generateSalt(): String = {
      val random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.currentTimeMillis())
      "%08x".format(random.nextInt())
    }
    val salt = generateSalt()
    val sha1base64Encoded = sha1(secret + salt)
    (sha1base64Encoded + SEPARATOR + salt)
  }
  def sha1(from: String): String = {
    val digester = MessageDigest.getInstance("SHA")
    digester.update(from.getBytes("UTF-8"))
    new BASE64Encoder().encode(digester.digest())
  }
}