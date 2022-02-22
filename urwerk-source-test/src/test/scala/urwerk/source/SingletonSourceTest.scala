package urwerk.source

import urwerk.source.test.*
import urwerk.test.TestBase

import java.util.concurrent.CompletableFuture
import scala.concurrent.Future
import scala.util.Try
import java.io.IOException

class SingletonSourceTest extends TestBase:

  "apply one element" in {
    SingletonSourceVerifier(
        SingletonSource(7))
      .expectNext(7)
      .verifyComplete()
  }

  "block" in {
    SingletonSource(7).block should be (7)
  }

  "block with IOExcepion" in {
    intercept[IOException]{
      SingletonSource.error(IOException())
        .block
    }
  }

  "defer" in {
    val sources = Iterator(
      SingletonSource(7),
      SingletonSource.error(new IllegalArgumentException()),
      SingletonSource(4))

    val source = SingletonSource.defer(
      sources.next)

    SingletonSourceVerifier(source)
      .expectNext(7)
      .verifyComplete()

    SingletonSourceVerifier(source)
      .expectError(classOf[IllegalArgumentException])
      .verify()

    SingletonSourceVerifier(source)
      .expectNext(4)
      .verifyComplete()
  }

  "error" in {
    SingletonSourceVerifier(
        SingletonSource.error(IllegalArgumentException()))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "filter true" in {
    OptionSourceVerifier(
        SingletonSource(42)
          .filter(_ == 42))
      .expectNext(42)
      .verifyComplete()
  }

  "filter false" in {
    OptionSourceVerifier(
        SingletonSource(42)
          .filter(_ != 42))
      .verifyComplete()
  }

  "filter not true" in {
    OptionSourceVerifier(
        SingletonSource(42)
          .filterNot(_ == 42))
      .verifyComplete()
  }

  "filter not false" in {
    OptionSourceVerifier(
        SingletonSource(42)
          .filterNot(_ != 42))
      .expectNext(42)
      .verifyComplete()
  }

  "flatMap with SingletonSource" in {
    SingletonSourceVerifier(
        SingletonSource(42)
          .flatMap(item => SingletonSource(item + 1)))
      .expectNext(43)
      .verifyComplete()
  }

  "flatMap with source" in {
    SourceVerifier(
        SingletonSource(1)
          .flatMap(item => Source(s"a:$item", s"b:$item")))
      .expectNext("a:1", "b:1")
      .verifyComplete()
  }

  "from completable future" in {
    val value = SingletonSource.from(
        CompletableFuture.completedFuture(77))
      .block
    value should be (77)
  }

  "from successful future" in {
    val value = SingletonSource.from(
            Future.successful(77))
      .block
    value should be (77)
  }

  "from failed future" in {
    SingletonSourceVerifier(
        SingletonSource.from(
          Future.failed(IllegalArgumentException())))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "from successful try" in {
    val value = SingletonSource.from(
        Try(77))
      .block
    value should be (77)
  }

  "from failed try" in {
    SingletonSourceVerifier(
        SingletonSource.from(
          Try(throw IllegalArgumentException())))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }
  "map" in {
    SingletonSourceVerifier(
        SingletonSource(1)
          .map(_.toString))
      .expectNext("1")
      .verifyComplete()
  }