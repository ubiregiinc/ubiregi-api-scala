package com.ubiregi.api.json
import scala.util.DynamicVariable
import net.liftweb.json.JsonAST

/** == Overview ==
 * Provides a DSL for constructing JSON object(based on [[net.liftweb.json.JsonAST.JValue]]).
 * To use the DSL, the following code is needed for preparation:
 * 
 * {{{
 * val builder = new JsonBuilder
 * import builder._
 * }}}
 * 
 * After the above code, users construct JSON object as the followings:
 * {{{
 * val jsonObject = %{
 *   'name :- "A Person"
 *   'age :- 28
 *   'nick_names :- $("foo", "hoge", "bar") 
 * }
 * }}}
 * 
 * To prevent pollution of namespace, it is recommended that this DSL is used
 * inside blocks:
 * {{{
 * {
 *   val builder = new JsonBuidler
 *   import builder._
 *   val person = %{'name :- "A Person"; 'age :- 28}
 * }
 * }}}
 */
class JsonBuilder {
  private[this] val values = new DynamicVariable[List[JsonAST.JField]](null)
  class Binding(receiver: String) {
    def :-(value: JsonAST.JValue) {
      values.value = JsonAST.JField(receiver, value) :: values.value
    }
  }
  
  implicit def pimpBindingFromString(arg: String): Binding = new Binding(arg)
  
  implicit def pimpBindingFromSymbol(arg: Symbol): Binding = new Binding(arg.name)
  
  implicit def int2JInt(arg: Int): JsonAST.JInt = JsonAST.JInt(arg)
  
  implicit def string2JString(arg: String): JsonAST.JString = JsonAST.JString(arg)
  
  implicit def boolean2JBool(arg: Boolean): JsonAST.JBool = JsonAST.JBool(arg)
  
  implicit def double2JDouble(arg: Double): JsonAST.JDouble = JsonAST.JDouble(arg)
  
  def %(body: => Any): JsonAST.JObject = {
    values.withValue(List()) {
      body
      JsonAST.JObject(values.value)
    }
  }
  
  def $(elements: JsonAST.JValue*): JsonAST.JArray = JsonAST.JArray(elements.toList)
}

/**
 * It is shorthand of (new JsonBuilder).
 * You can use JsonBuilder object instead of (new JsonBuilder) as the followings:
 * {{{
 * import com.ubiregi.api.json.JsonBuilder._
 * }}}
 */
object JsonBuilder extends JsonBuilder