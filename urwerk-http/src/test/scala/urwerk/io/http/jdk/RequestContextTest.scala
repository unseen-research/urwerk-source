package urwerk.io.http.jdk

import com.github.tomakehurst.wiremock.client.WireMock.*
import urwerk.io.http
import urwerk.io.http.Response
import urwerk.source.test.SourceVerifier
import urwerk.source.{SingletonSource, Source}
import urwerk.test.{HttpServer, TestBase}

import scala.util.Random

class RequestContextTest extends TestBase with HttpServer:

  "response content is disposed when not subscribed while inside the response operator chain" in {
    val givenBytes = Random.nextBytes(10)
    var completed = false

    httpServer.stubFor(get(s"/get/resource")
      .willReturn(aResponse()
        .withBody(givenBytes)
        .withStatus(203)))

    val contentSource = RequestContext(http.Request.Get(s"${serverUrl}/get/resource"))
      .response
      .map(_.content).block

    // TODO
    // This workaround is required because the content disposing subscription is possibly later than the
    // subscription outside this operator chain below. The disposing subscription is running in the underlying http clients
    // thread which is differs from this test thread.
    // The sleep statement just ensures the disposing subscription happens before the test subscription.
    // Catching the IllegalStateException shows that the response content is closed automatically when not drained
    // inside the operator chain of the response method.

    Thread.sleep(10)

    intercept[IllegalStateException] {
      contentSource.last.block should be(givenBytes)
    }
  }
