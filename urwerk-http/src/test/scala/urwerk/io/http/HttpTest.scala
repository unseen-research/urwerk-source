package urwerk.io.http

import com.github.tomakehurst.wiremock.client.WireMock.*

import urwerk.test.TestBase
import urwerk.test.HttpServer
import scala.util.Random

class HttpTest extends TestBase with HttpServer:

  "http" in {
    val givenBytes = Random.nextBytes(10)

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)))
    
    val receivedBytes = Http.get(s"${serverUrl}/get/resource")
      .bytes.toSeq.block.head

    receivedBytes should be (givenBytes)
  }