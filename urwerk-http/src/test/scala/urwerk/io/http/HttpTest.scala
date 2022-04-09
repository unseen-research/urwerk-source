package urwerk.io.http

import com.github.tomakehurst.wiremock.client.WireMock.*
import urwerk.io.http
import urwerk.source.test.SourceVerifier
import urwerk.source.{SingletonSource, Source}
import urwerk.test.{HttpServer, TestBase}

import scala.util.Random

class HttpTest extends TestBase with HttpServer:

  "http get bytes" in {
    val givenBytes = Random.nextBytes(10)
    val givenUri = s"get/${Random.nextInt}"

    httpServer.stubFor(get(s"/$givenUri")
      .willReturn(aResponse()
        .withBody(givenBytes)))
    
    val receivedBytes = http.Get(s"${serverUrl}/$givenUri")
      .bytes
      .toSeq.block.head

    receivedBytes should be (givenBytes)
  }

  "http get bytes fails with http error status code" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)
        .withStatus(404)))
    
    val body = http.Get(s"${serverUrl}/get/resource")
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
    
    val responseContent = http.Get(s"${serverUrl}/get/resource")
      .response
      .filter(_.statusCode == 203)
      .flatMap(_.content)
      .toSeq
      .last.block.head

    responseContent should be (givenBytes)
  }
