package urwerk.io.http

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.util.{List => juList}
import java.util.concurrent.Flow.Publisher

import scala.collection.compat.immutable.ArraySeq
import scala.jdk.CollectionConverters._

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

  def get(uri: String): Requester = new Requester{
    def bytes: Source[Seq[Byte]] = 
      val client = HttpClient.newBuilder()
        .build()

      val request = HttpRequest.newBuilder()
        .method("GET", BodyPublishers.noBody())
        .uri(Uri(uri))
        .build

      val reponseFuture = client.sendAsync(request, BodyHandlers.ofPublisher())

      SingletonSource.from(reponseFuture)   
        .flatMap(response => mapContent(response))
  }

  private def mapContent(response: HttpResponse[Publisher[juList[ByteBuffer]]]): Source[Seq[Byte]] =

    Source.from(response.body)
      .flatMap{buffers =>
        Source.from(buffers.asScala)}
      .map{buffer => 
        val bytes = Array.ofDim[Byte](buffer.remaining)
        buffer.get(bytes)
        ArraySeq.unsafeWrapArray(bytes)
      }

  
  def apply(uri: String): Requester = ???
  
  def request(uri: Uri, method: Method = Method.Get): Requester = ???

  def request(request: Request): Requester = ???

trait Requester:
  def bytes: Source[Seq[Byte]]

  // def string: Source[Seq[String]]

  // def lines: Source[Seq[String]]

  // def response: SingletonSource[Response]
