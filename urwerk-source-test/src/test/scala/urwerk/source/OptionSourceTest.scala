package urwerk.source

import urwerk.source.test.*
import urwerk.test.TestBase
import java.io.IOException

class OptionSourceTest extends TestBase:

  "apply one element" in {
    OptionSource(7).toVerifier
      .expectNext(7)
      .verifyComplete()
  }

  "apply no element" in {
    OptionSourceVerifier(
        OptionSource())
      .verifyComplete()
  }

  "apply some element" in {
    OptionSourceVerifier(
        OptionSource(Some(7)))
      .expectNext(7)
      .verifyComplete()
  }

  "apply none element" in {
    OptionSourceVerifier(
        OptionSource(None))
      .verifyComplete()
  }

  "block some element" in {
    OptionSource(1).block should be(Some(1))
  }

  "block none" in {
    OptionSource().block should be(None)
  }

  "block with IOExcepion" in {
    intercept[IOException]{
      OptionSource.error(IOException())
        .block
    }
  }

  "empty" in {
    OptionSourceVerifier(
        OptionSource.empty[Int])
      .verifyComplete()
  }

  "error" in {
    OptionSourceVerifier(
        OptionSource.error(IllegalArgumentException()))
      .expectError(classOf[IllegalArgumentException])
      .verify()
  }

  "filter true" in {
    OptionSourceVerifier(
        OptionSource(42)
         .filter(_ == 42))
      .expectNext(42)
      .verifyComplete()
  }

  "filter false" in {
    OptionSourceVerifier(
        OptionSource(42)
          .filter(_ != 42))
      .verifyComplete()
  }

  "filter not true" in {
    OptionSourceVerifier(
        OptionSource(42)
          .filterNot(_ == 42))
      .verifyComplete()
  }

  "filter not false" in {
    OptionSourceVerifier(
        OptionSource(42)
          .filterNot(_ != 42))
      .expectNext(42)
      .verifyComplete()
  }

  "flatMap with OptionSource" in {
    OptionSourceVerifier(
        OptionSource(42)
          .flatMap(item => OptionSource(item + 1)))
      .expectNext(43)
      .verifyComplete()
  }

  "flatMap with source" in {
    SourceVerifier(
        OptionSource(1)
          .flatMap(item => Source(s"a:$item", s"b:$item")))
      .expectNext("a:1", "b:1")
      .verifyComplete()
  }

  "map" in {
    OptionSourceVerifier(
      OptionSource(1)
        .map(_.toString))
      .expectNext("1")
      .verifyComplete()
  }