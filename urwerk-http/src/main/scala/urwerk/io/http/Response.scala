package urwerk.io.http

import urwerk.source.Source

import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.util.List as JavaList
import java.util.concurrent.Flow.Publisher
import scala.collection.compat.immutable.ArraySeq
import scala.jdk.CollectionConverters.*

object Response:
  private[http] def fromHttResponse(response: HttpResponse[Publisher[JavaList[ByteBuffer]]]): Response =
    val content = Source.from(response.body)
      .map(_.asScala)
      .flatMap(buffers => Source.from(buffers))
      .map{buffer => 
        val bytes = Array.ofDim[Byte](buffer.remaining)
        buffer.get(bytes)
        ArraySeq.unsafeWrapArray(bytes)
      }

    Response(response.statusCode, content)

case class Response(statusCode: Int, content: Source[Seq[Byte]])