package com.ubiregi.api

import java.security.SecureRandom
import java.security.MessageDigest

package object security {
  def encrypt(secret: String): (Array[Byte], String) = {
    def generateSalt(): String = {
      val random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.currentTimeMillis())
      "%x".format(random.nextInt())
    }
    val salt = generateSalt()
    val digester = MessageDigest.getInstance("SHA")
    digester.update((salt + secret).getBytes())
    (digester.digest(), salt)
  }
}