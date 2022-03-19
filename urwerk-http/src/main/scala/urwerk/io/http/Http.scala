package urwerk.io.http

import urwerk.io.Uri

import urwerk.source.Source
import urwerk.source.SingletonSource

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

object Http extends Http

trait Attributes

trait AttributeSpec[A <: String]:
  type V

trait Response:
  def attribute[A <: String](using as: AttributeSpec[A]): as.V

trait Http:
  def request(uri: Uri, method: Method = Method.Get): Requester = ???

  def request(request: Request): Requester = ???

trait Requester:
  def bytes: Source[Seq[Byte]]

  def string: Source[Seq[String]]

  def lines: Source[Seq[String]]

  def response: SingletonSource[Response]
