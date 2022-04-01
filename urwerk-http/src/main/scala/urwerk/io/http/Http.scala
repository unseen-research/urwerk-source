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

object Http extends Http

trait Attributes

trait AttributeSpec[A <: String]:
  type V

trait Http:

  def get(uri: String): Requester = new Requester{
    def bytes: Source[Seq[Byte]] = 
      response
        .doOnNext{response => 
          val statusCode = response.statusCode
          if statusCode > 299 then
            throw HttpStatusException(statusCode)
        }
        .flatMap(_.content)

    def response: SingletonSource[Response] = 
      val client = HttpClient.newBuilder()
        .build()

      val request = HttpRequest.newBuilder()
        .method("GET", BodyPublishers.noBody())
        .uri(Uri(uri))
        .build

      val reponseFuture = client.sendAsync(request, BodyHandlers.ofPublisher())

      SingletonSource.from(reponseFuture)   
        .map(Response.fromHttResponse)
  }

  private def xxx(response: HttpResponse[Publisher[juList[ByteBuffer]]]): Response = ???

  def apply(uri: String): Requester = ???
  
  def request(uri: Uri, method: Method = Method.Get): Requester = ???

  def request(request: Request): Requester = ???

trait Requester:
  def bytes: Source[Seq[Byte]]

  // def string: Source[Seq[String]]

  // def lines: Source[Seq[String]]

  def response: SingletonSource[Response]
