package urwerk.io.http

import urwerk.source.Source
import urwerk.source.SingletonSource

object Http

trait Attributes

trait AttributeSpec[A <: String]:
  type V

trait Response:
  def attribute[A <: String](using as: AttributeSpec[A]): as.V

trait Http:
  def bytes: Source[Seq[Byte]]

  def string: Source[Seq[String]]

  def lines: Source[Seq[String]]

  def response: SingletonSource[Response]
