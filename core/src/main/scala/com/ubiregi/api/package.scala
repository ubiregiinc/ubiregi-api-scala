package com.ubiregi
import java.io.InputStream
import java.io.FileInputStream
import dispatch.classic.HttpExecutor

/**
 * Provides type aliases, utility functions, and so on for using UbiregiClient.
 */
package object api {
  type Id[T] = T
  type Executor[TC[_]] = HttpExecutor { type HttpPackage[T] = TC[T] }
  type StringMap = Map[String, String]
  type RequestHeader = Map[String, String]
  type BASE64Encoder = sun.misc.BASE64Encoder
  type BASE64Decoder = sun.misc.BASE64Decoder
  type ==>[-A, +B] = PartialFunction[A, B]

  val Catcher = scala.util.control.Exception

  //---- Utility Functions ----
  def encodeBASE64(source: String, encoding: String = "UTF-8"): String = {
    new BASE64Encoder().encode(source.getBytes(encoding))
  }
  def decodeBASE64(source: String, encoding: String = "UTF-8"): String = {
    new String(new BASE64Decoder().decodeBuffer(source), encoding)
  }
  def openStream[A](path: String)(block: InputStream => A): A = {
    val stream = new FileInputStream(path)
    try { block(stream) } finally { Catcher.allCatch(stream.close()) }
  }
  def readBytes(in: InputStream): Array[Byte] = {
    Iterator.continually(in.read()).takeWhile(_ != -1).map(_.toByte).toArray
  }
}
