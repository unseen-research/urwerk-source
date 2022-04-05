package urwerk.io.http

import com.github.tomakehurst.wiremock.client.WireMock.*
import urwerk.source.test.SourceVerifier
import urwerk.test.{HttpServer, TestBase}
import urwerk.source.{SingletonSource, Source}

import scala.util.Random

class HttpTest extends TestBase with HttpServer:

//  "source test" in {
//    val inner = Source(1, 2, 3)
//      .doOnEach(signal => println(s"Inner $signal"))
//
//    val outer = SingletonSource(inner)
//      .doOnEach(signal => println(s"Outer $signal"))
//      .flatMap(source => source)
//      .doOnEach(signal => println(s"Outer2 $signal"))
//      .map(v => s"Val $v")
//
//
//    val top = Source.from(outer.toPublisher)
//      .doOnEach(s => println(s"Top $s"))
//
//    top.last.block
//  }

  "http get bytes" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)))
    
    val receivedBytes = Http.get(s"${serverUrl}/get/resource")
      .bytes.toSeq.block.head

    receivedBytes should be (givenBytes)
  }

  "http get bytes fails with http error status code" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)
        .withStatus(404)))
    
    val body = Http.get(s"${serverUrl}/get/resource")
      .bytes 
    
    SourceVerifier(body)
      .expectErrorMatches(error =>
          error.isInstanceOf[HttpStatusException]
            && error.asInstanceOf[HttpStatusException].statusCode == 404)
        .verify()    
  }

  "http get response" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)
        .withStatus(203)))
    
    val responseContent = Http.get(s"${serverUrl}/get/resource")
      .response
      .filter(_.statusCode == 203)
      .flatMap(_.content)
      .toSeq
      .block.head

    responseContent should be (givenBytes)
  }

  "http response close content source if not subscribed to it" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)
        .withStatus(203)))

    val response = Http.get(s"${serverUrl}/get/resource")
      .response.block

    response.statusCode should be (203)
    response.content.toSeq.block.head should be (givenBytes)
  }
