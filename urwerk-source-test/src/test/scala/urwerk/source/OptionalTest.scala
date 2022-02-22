package urwerk.source

import urwerk.source.test.*
import urwerk.test.TestBase
import java.io.IOException

class OptionalTest extends TestBase:

  "apply one element" in {
    Optional(7).toVerifier
      .expectNext(7)
      .verifyComplete()
  }

  "apply no element" in {
    OptionalVerifier(
        Optional())
      .verifyComplete()
  }

  "apply some element" in {
    OptionalVerifier(
        Optional(Some(7)))
      .expectNext(7)
      .verifyComplete()
  }

  "apply none element" in {
    OptionalVerifier(
        Optional(None))
      .verifyComplete()
  }

  "block some element" in {
    Optional(1).block should be(Some(1))
  }

  "block none" in {
    Optional().block should be(None)
  }

  "block with IOExcepion" in {
    intercept[IOException]{
      Optional.error(IOException())
        .block
    }
  }

  "empty" in {
    OptionalVerifier(
        Optional.empty[Int])
      .verifyComplete()
  }

  "error" in {
    OptionalVerifier(
        Optional.error(IllegalArgumentException()))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "filter true" in {
    OptionalVerifier(
        Optional(42)
         .filter(_ == 42))
      .expectNext(42)
      .verifyComplete()
  }

  "filter false" in {
    OptionalVerifier(
        Optional(42)
          .filter(_ != 42))
      .verifyComplete()
  }

  "filter not true" in {
    OptionalVerifier(
        Optional(42)
          .filterNot(_ == 42))
      .verifyComplete()
  }

  "filter not false" in {
    OptionalVerifier(
        Optional(42)
          .filterNot(_ != 42))
      .expectNext(42)
      .verifyComplete()
  }

  "flatMap with optional" in {
    OptionalVerifier(
        Optional(42)
          .flatMap(item => Optional(item + 1)))
      .expectNext(43)
      .verifyComplete()
  }

  "flatMap with source" in {
    SourceVerifier(
        Optional(1)
          .flatMap(item => Source(s"a:$item", s"b:$item")))
      .expectNext("a:1", "b:1")
      .verifyComplete()
  }

  "map" in {
    OptionalVerifier(
      Optional(1)
        .map(_.toString))
      .expectNext("1")
      .verifyComplete()
  }