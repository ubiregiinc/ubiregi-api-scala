package com.ubiregi.api

import dispatch._
import dispatch./._
import dispatch.json._
import java.io.File
import net.liftweb.json.JsonParser
import net.liftweb.json.JsonAST
import UbiregiClient._

/** ==Overview==
 * Provides features to access Ubiregi API easily.
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
class UbiregiClient private(val endpoint: String, val secret: String, val apiToken: String, val userAgent: String, val executor: Http) {
    
  /** Does shutdown of this client.
   *  Once this method is called, this client cannot be reusable.
   */
  def shutdown(): Unit = {
    executor.shutdown()
  }
    
  /** Request headers for calling Ubiregi API.  The value of the variable
   *  is determined by userAgent, apiToken, secret.
   */
  val defaultHeaders: RequestHeader = Map(
    USER_AGENT -> userAgent,
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
  
  /** Does GET request on Ubiregi API.  This method returns response as String.  Typically, the return value is JSON-formatted String.
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def rawGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader): String = {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders ++ extHeaders
    executor((url(urlOrPath) <:< headers) >- {s => s})
  }
  
  /** Does GET request on Ubiregi API.  The only difference from rawPost() is that this method doesn't return String, but [[net.liftweb.json.JsonAST.JArray]].
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def jsonGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): JsonAST.JValue = {
    val responseBody = rawGet(urlOrPathInit, query, extHeaders)
    JsonParser.parse(responseBody)
  }
  
  /** Does POST request on Ubiregi API.  This method returns response as String.  Typically, the return value is JSON-formatted String.
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param content used for request.  content must be JSON formatted string.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def rawPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): String = {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders + (CONTENT_TYPE -> APPLICATION_JSON) ++ extHeaders
    val result = executor((url(urlOrPath).POST << (content.toString()) <:< headers) >- (s => s))
    result
  }
    
  /** Does POST request on Ubiregi API.  The only difference from rawPost() is that this method doesn't return String, but [[net.liftweb.json.JsonAST.JArray]].
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param content used for request.  content must be JSON-formatted string.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.  Typically, the type of the return value is [[net.liftweb.json.JsonAST.JArray]].
   */
  def jsonPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): JsonAST.JValue = {
    val responseBody = rawPost(urlOrPathInit, content, query, extHeaders)
    JsonParser.parse(responseBody)
  }
}

object UbiregiClient {
  /** Constructs a new instance of [[com.ubiregi.api.UbiregiClient]].
   *  For creating a new instance of [[com.ubiregi.api.UbiregiClient]], it is recommended
   *  to use this method rather than new UbiegiClient(...).
   *  
   *  @param endpoint endpoint of Ubiregi API.
   *  @param secret secret token provided when you registered your app in "Ubiregi for Developers" site.
   *  @param apiToken api token provided when you installed your app in "Ubiregi for Developers" site.
   */
  def apply(endpoint: String, secret: String, apiToken: String, userAgent: String = DEFAULT_USER_AGENT_NAME, executor: Http = new Http): UbiregiClient = {
    new UbiregiClient(endpoint, secret, apiToken, userAgent, executor)
  }
  final val USER_AGENT = "User-Agent"
  final val DEFAULT_USER_AGENT_NAME = "Ubiregi-API-Client-in-Scala; en"
  final val AUTH_TOKEN = "X-Ubiregi-Auth-Token"
  final val APP_SECRET = "X-Ubiregi-App-Secret"
  final val CONTENT_TYPE = "Content-Type"
  final val APPLICATION_JSON = "application/json"
  final val HTTP_PREFIX_PATTERN = """^http.*"""
}