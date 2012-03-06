package com.ubiregi.api

import dispatch._
import dispatch./._
import dispatch.json._
import java.io.File
import net.liftweb.json.JsonParser
import net.liftweb.json.JsonAST
import UbiregiClient._

/** ==Overview==
 * Provides features to access Ubiregi API(version 3) easily.
 * Cumbersome procedures for accessing Ubiregi API are encapsulated in this class.
 * 
 * Typical use case is as the followings:
 * {{{
 * val client = UbiregiClient("http://localhost:3030/api/3/", "secret", "apiToken")
 * val response: JsonAST.JValue = client.jsonGet("accounts/current")
 * }}}
 * 
 * Note that response is not a string, but a value of [[net.liftweb.json.JsonAST.JValue]] 
 * when using  UbiregiClient#jsonGet() or UbiregiClient#jsonPost().
 * 
 * If you want to get the string response, you can use UbiregiClient#rawGet()
 * or UbiregiClient#rawPost() instead.
 */
class UbiregiClient(val endpoint: String, val secret: String, val apiToken: String) {
  private[this] val client: Http = new Http()
    
  def shutdown(): Unit = {
    client.shutdown()
  }
    
  val defaultHeaders: RequestHeader = Map(
    USER_AGENT -> "Ubiregi-API-Client-in-Scala; en",
    AUTH_TOKEN -> apiToken,
    APP_SECRET -> secret
  )
    
  def jsonIndex(url: String, collection: String, acc: List[JsonAST.JValue] = Nil, blockOpt: Option[JsonAST.JValue=> Any] = None): List[JsonAST.JValue]= {
    val response = jsonGet(url)
    for(block <- blockOpt) block(response)
    response \\ "next-url" match {
      case JsonAST.JString(nextUrl) =>
        jsonIndex(nextUrl, collection, (response \\ collection) :: acc)
      case _ =>
        ((response \\ collection) :: acc).reverse
    }
  }
  
  def rawGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader): String = {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders ++ extHeaders
    client((url(urlOrPath) <:< headers) >- {s => s})
  }
  
  def jsonGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): JsonAST.JValue = {
    val responseBody = rawGet(urlOrPathInit, query, extHeaders)
    JsonParser.parse(responseBody)
  }
  
  def rawPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): String = {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders + (CONTENT_TYPE -> APPLICATION_JSON) ++ extHeaders
    val result = client((url(urlOrPath).POST << (content.toString()) <:< headers) >- (s => s))
    result
  }
    
  def jsonPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): JsonAST.JValue = {
    val responseBody = rawPost(urlOrPathInit, content, query, extHeaders)
    JsonParser.parse(responseBody)
  }
}
object UbiregiClient {
  def apply(endpoint: String, secret: String, apiToken: String): UbiregiClient = {
    new UbiregiClient(endpoint, secret, apiToken)
  }
  final val USER_AGENT = "User-Agent"
  final val AUTH_TOKEN = "X-Ubiregi-Auth-Token"
  final val APP_SECRET = "X-Ubiregi-App-Secret"
  final val CONTENT_TYPE = "Content-Type"
  final val APPLICATION_JSON = "application/json"
  final val HTTP_PREFIX_PATTERN = """^http.*"""
}