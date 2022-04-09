package urwerk.io.http

import urwerk.io.Uri
import urwerk.source.{SingletonSource, Source}

import java.net.http.HttpRequest.BodyPublishers
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import java.nio.ByteBuffer
import java.util.List as juList
import java.util.concurrent.Flow.Publisher
import scala.collection.compat.immutable.ArraySeq
import scala.jdk.CollectionConverters.*

enum Method(val name: String):
  case Get  extends Method("GET")
  case Head  extends Method("HEAD")
  case Post  extends Method("POST")
  case Put extends Method("PUT")
  case Delete extends Method("DELETE")
  case Connect extends Method("CONNECT")
  case Options extends Method("OPTIONS")
  case Trace extends Method("TRACE")
  case Other(method: String) extends Method(method)

object Get:
  def apply(uri: String): RequestContext = jdk.RequestContext(Request.Get(uri))

case class Attribute(name: String, value: String)

trait Attributes

trait AttributeSpec[A <: String]:
  type V

trait RequestContext:
  def bytes: Source[Seq[Byte]]

  def attribut(name: String) = ???

  // def string: Source[Seq[String]]

  // def lines: Source[Seq[String]]

  def response: SingletonSource[Response]
