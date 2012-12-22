package com.ubiregi.example

import dispatch.classic.Http
import net.liftweb.json.JsonAST
import com.ubiregi.api._

/**
 * An example program using UbiregiClient.
 */
object UbiregiClientExample {
  def main(args: Array[String]): Unit = {
    val Array(endpoint, secret, apiToken) = args
    val client = UbiregiClient[Id](endpoint, secret, apiToken)
    val json =  client.jsonGet("""accounts/current""")
    println(json)
  }
}
