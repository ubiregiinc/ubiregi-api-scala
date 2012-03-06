package com.ubiregi.api

object UbiregiClientExample {
  def main(args: Array[String]): Unit = {
    val Array(endpoint, secret, apiToken) = args
    val client = new UbiregiClient(endpoint, secret, apiToken)
    val json = client.jsonGet("""accounts/current""")
    println(json)
  }
}