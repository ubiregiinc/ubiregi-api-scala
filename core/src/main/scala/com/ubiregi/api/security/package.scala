package com.ubiregi.api

import java.math.BigInteger
import java.security.SecureRandom
import java.security.MessageDigest

package object security {
  private final val SEPARATOR = ":"
  def encode_secret(secret: String): String = {
    def generateSalt(): String = {
      val random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.currentTimeMillis())
      "%08x".format(random.nextInt())
    }
    val salt = generateSalt()
    val hexDigest = sha1HexDigest(salt + secret)
    (salt + SEPARATOR + hexDigest)
  }
  def sha1HexDigest(from: String): String = {
    val sha1 = MessageDigest.getInstance("SHA")
    sha1.reset()
    sha1.update(from.getBytes("UTF-8"))
    val xs = sha1.digest()
    xs.map{n => "%02x".format(n & 0xff)}.mkString
  }
}