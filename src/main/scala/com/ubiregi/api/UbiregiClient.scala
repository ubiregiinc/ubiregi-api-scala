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
 * val client = UbiregiClient[Id, Http]("http://localhost:3030/api/3/", "secret", "apiToken")
 * val response: JsonAST.JValue = client.jsonGet("accounts/current")
 * }}}
 * 
 * Note that response is not a String, but a value of [[net.liftweb.json.JsonAST.JValue]] 
 * when using  UbiregiClient#jsonGet() or UbiregiClient#jsonPost() in this case.
 * 
 * If you want to get the string response, you can use UbiregiClient#rawGet()
 * or UbiregiClient#rawPost() instead.
 * 
 * This type constructor parameter TC is used as [[dispatch.HttpExecutor { type HttpPackage[T] = TC[T] }]].
 * If your HttpExecutor#HttpPackage[T] is just T, you must specify TC as [[com.ubiregi.api.Id]].
 * If your HttpExecutor#HttpPackage[T] is Future[T], you must specify TC as Future.
 */
class UbiregiClient[TC[_], E <: Executor[TC]] private(val endpoint: String, val secret: String, val apiToken: String, val userAgent: String, val executor: E) {
    
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
    
  def _get[X](urlOrPathInit: String, query: StringMap, extHeaders: RequestHeader, convertor: String => X): TC[X] = {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders ++ extHeaders
    executor((url(urlOrPath) <:< headers) >- convertor)
  }
  
  def _post[X](urlOrPathInit: String, content: String, query: StringMap, extHeaders: RequestHeader = Map(), convertor: String => X): TC[X]= {
    val urlOrPath = if (urlOrPathInit.matches(HTTP_PREFIX_PATTERN)) urlOrPathInit else endpoint + urlOrPathInit
    val headers = defaultHeaders + (CONTENT_TYPE -> APPLICATION_JSON) ++ extHeaders
    executor((url(urlOrPath).POST << (content.toString()) <:< headers) >- convertor)
  }
  
  /** Does GET request on Ubiregi API.  This method returns response as TC[String].  Typically, the return value is JSON-formatted String.
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def rawGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader): E#HttpPackage[String] = {
    _get(urlOrPathInit, query, extHeaders, {s => s})
  }
  
  /** Does GET request on Ubiregi API.  The only difference from rawPost() is that this method doesn't return TC[String], but TC[ [[net.liftweb.json.JsonAST.JArray]] ].
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def jsonGet(urlOrPathInit: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): TC[JsonAST.JValue] = {
    _get(urlOrPathInit, query, extHeaders, {s => JsonParser.parse(s)})
  }
  
  /** Does POST request on Ubiregi API.  This method returns response as TC[String].  Typically, the return value is JSON-formatted String.
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param content used for request.  content must be JSON formatted string.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.
   */
  def rawPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): E#HttpPackage[String] = {
    _post(urlOrPathInit, content, query, extHeaders, {s => s})
  }
    
  /** Does POST request on Ubiregi API.  The only difference from rawPost() is that this method doesn't return TC[String], but TC[ [[net.liftweb.json.JsonAST.JArray]] ].
   * 
   * @param urlOrPathInit URL for requesting or relative path to endpoint.  if urlOrPathInit begins "http", then it is regarded as URL.  Otherwise, relative path to endpoint.
   * @param content used for request.  content must be JSON-formatted string.
   * @param query for reservation and is not used currently.
   * @param extHeaders HTTP request headers that users want to provide additionally.
   * @return Result of this request.  Typically, the type of the return value is [[net.liftweb.json.JsonAST.JArray]].
   */
  def jsonPost(urlOrPathInit: String, content: String, query: StringMap = Map(), extHeaders: RequestHeader = Map()): E#HttpPackage[JsonAST.JValue] = {
    _post(urlOrPathInit, content, query, extHeaders, {s => JsonParser.parse(s)})
  }
}

object UbiregiClient {
  /** Constructs a new instance of [[com.ubiregi.api.UbiregiClient]].
   *  For creating a new instance of [[com.ubiregi.api.UbiregiClient]], it is needed
   *  to use this method not new UbiregiClient(...).
   *  
   *  Typical use case is as followings:
   *  {{{
   *    val client = UbiregiClient[Id, Http](...)
   *    val json = client.jsonGet("accounts/account")
   *    println(json \\ "id")
   *  }}}
   *  
   *  If your executor's execution result is not the plain type, you can code
   *  as folloinwgs:
   *  {{{
   *    val client = UbiregiClient[Future, FutureHttp](...)
   *    val jsonFuture: Future[JsonAST.JValue] = client.jsonGet("accounts/account")
   *    ...
   *  }}}
   *  
   *  @param endpoint Endpoint of Ubiregi API.
   *  @param secret secret token provided when you registered your app in "Ubiregi for Developers" site.
   *  @param apiToken API token provided when you installed your app in "Ubiregi for Developers" site.
   *  @param userAgent content of User-Agent header. if not specified, [[DEFAULT_USER_AGENT_NAME]] is used.
   *  @param executor Executor in dispatch. if not specified, instance of [[dispatch.Http]] is used.  If you this library in Google App Engine,
   *  an instance of [[dispatch.gae.Http]] can be specified instead of [[dispatch.Http]].
   */
  def apply[TC[_], E <: Executor[TC]](endpoint: String, secret: String, apiToken: String, userAgent: String = DEFAULT_USER_AGENT_NAME, executor: E = new Http): UbiregiClient[TC, E] = {
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