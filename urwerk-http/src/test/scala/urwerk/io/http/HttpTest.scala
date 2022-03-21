package urwerk.io.http

import com.github.tomakehurst.wiremock.client.WireMock.*

import scala.util.Random

import urwerk.source.test.SourceVerifier
import urwerk.test.TestBase
import urwerk.test.HttpServer

class HttpTest extends TestBase with HttpServer:

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
    
    val response = Http.get(s"${serverUrl}/get/resource")
      .response.block

    response.statusCode should be (203)

    val receivedBytes = Http.get(s"${serverUrl}/get/resource")
      .bytes.toSeq.block.head

    receivedBytes should be (givenBytes)
  }