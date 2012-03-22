/**
 *
 */
package com.ubiregi.api.security
import org.specs2.mutable.Specification
import net.liftweb.json.JsonAST

/**
 * @author Mizushima
 *
 */
class EncryptSpecification extends Specification {
  """assume secret"""" should {
    val secret = "abcdefg"
    val Array(pass, salt) = encrypt(secret).split(":")
    
    """sha1HexDigest("abc")""" in {
      val secret = "abc"
      sha1HexDigest(secret) must === ("a9993e364706816aba3e25717850c26c9cd0d89d")
    }
    
    """sha1HexDigest("abcdefg")""" in {
      val secret = "abcdefg"
      sha1HexDigest(secret) must === ("2fb5e13419fc89246865e7a324f476ec624e8740")
    }
      
    """sha1HexDigest(secret + salt)""" in {
      val secret = "abcdefg"
      val Array(pass, salt) = encrypt(secret).split(":")
      sha1HexDigest(secret + salt) must ===(pass)
    }
  }
}