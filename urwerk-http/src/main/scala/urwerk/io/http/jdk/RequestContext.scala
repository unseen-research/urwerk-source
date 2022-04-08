package urwerk.io.http.jdk

import urwerk.io.http.{HttpStatusException, Request, Response}
import urwerk.io.{Uri, http}
import urwerk.source.{Signal, SingletonSource, Source}

import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.util.concurrent.{Executors, Flow}
import scala.concurrent.ExecutionContext

object RequestContext:
  private def closeResponseContent(response: Response): Unit =
    response.content.subscribe(new Flow.Subscriber{
      def onComplete() = ()
      def onError(throwable: Throwable) = ()
      def onNext(value: Seq[Byte]) = ()
      override def onSubscribe(subscription: Flow.Subscription) =
        subscription.cancel()
    })

class RequestContext(request: Request) extends http.RequestContext:
  import RequestContext.*

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

    val _request = HttpRequest.newBuilder()
        .method("GET", BodyPublishers.noBody())
        .uri(request.uri)
        .build

    val responseFuture = client.sendAsync(_request, BodyHandlers.ofPublisher())

    SingletonSource.from(responseFuture)
      .map(Response.fromHttResponse)
      .flatMap(response =>
        SingletonSource(response)
          .doFinally{()=>
            closeResponseContent(response)
          })
