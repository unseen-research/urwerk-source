package urwerk.source

import urwerk.source.test.*
import urwerk.test.TestBase

import java.util.concurrent.CompletableFuture
import scala.concurrent.Future
import scala.util.Try
import java.io.IOException

class SingletonTest extends TestBase:

  "apply one element" in {
    SingletonVerifier(
        Singleton(7))
      .expectNext(7)
      .verifyComplete()
  }

  "block" in {
    Singleton(7).block should be (7)
  }

  "block with IOExcepion" in {
    intercept[IOException]{
      Singleton.error(IOException())
        .block
    }
  }

  "defer" in {
    val sources = Iterator(
      Singleton(7),
      Singleton.error(new IllegalArgumentException()),
      Singleton(4))

    val source = Singleton.defer(
      sources.next)

    SingletonVerifier(source)
      .expectNext(7)
      .verifyComplete()

    SingletonVerifier(source)
      .expectError(classOf[IllegalArgumentException])
      .verify()

    SingletonVerifier(source)
      .expectNext(4)
      .verifyComplete()
  }

  "error" in {
    SingletonVerifier(
        Singleton.error(IllegalArgumentException()))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "filter true" in {
    OptionalVerifier(
        Singleton(42)
          .filter(_ == 42))
      .expectNext(42)
      .verifyComplete()
  }

  "filter false" in {
    OptionalVerifier(
        Singleton(42)
          .filter(_ != 42))
      .verifyComplete()
  }

  "filter not true" in {
    OptionalVerifier(
        Singleton(42)
          .filterNot(_ == 42))
      .verifyComplete()
  }

  "filter not false" in {
    OptionalVerifier(
        Singleton(42)
          .filterNot(_ != 42))
      .expectNext(42)
      .verifyComplete()
  }

  "flatMap with singleton" in {
    SingletonVerifier(
        Singleton(42)
          .flatMap(item => Singleton(item + 1)))
      .expectNext(43)
      .verifyComplete()
  }

  "flatMap with source" in {
    SourceVerifier(
        Singleton(1)
          .flatMap(item => Source(s"a:$item", s"b:$item")))
      .expectNext("a:1", "b:1")
      .verifyComplete()
  }

  "from completable future" in {
    val value = Singleton.from(
        CompletableFuture.completedFuture(77))
      .block
    value should be (77)
  }

  "from successful future" in {
    val value = Singleton.from(
            Future.successful(77))
      .block
    value should be (77)
  }

  "from failed future" in {
    SingletonVerifier(
        Singleton.from(
          Future.failed(IllegalArgumentException())))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "from successful try" in {
    val value = Singleton.from(
        Try(77))
      .block
    value should be (77)
  }

  "from failed try" in {
    SingletonVerifier(
        Singleton.from(
          Try(throw IllegalArgumentException())))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }
  "map" in {
    SingletonVerifier(
        Singleton(1)
          .map(_.toString))
      .expectNext("1")
      .verifyComplete()
  }