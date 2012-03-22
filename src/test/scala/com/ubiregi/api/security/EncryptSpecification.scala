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
  """assume secret is "abcdefg"""" should {
    val secret = "abcdefg"
    val Array(pass, salt) = encrypt(secret).split(":")
      
    """BASE64 encoded sha1(secret + salt)""" in {
      sha1(secret + salt) must ===(pass)
    }
  }
}