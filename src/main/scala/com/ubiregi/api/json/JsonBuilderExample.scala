package com.ubiregi.api.json

/**
 * An example program using JsonBuilder.
 */
object JsonBuilderExample {
  def main(args: Array[String]): Unit = {
    val builder = new JsonBuilder
    import builder._
    val obj = %{
      "x" :- 10
      "y" :- 20
      "z" :- %{
        "a" :- $(1, 2, 3, 4, 5)
        "b" :- $(6, 7, %{ "xx" -> "yy" })
      }
    }
    println(obj)
  }
}