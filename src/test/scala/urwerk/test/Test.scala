package urwerk.test

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.io.OutputStream
import java.nio.file.{Files, Paths}
import java.util.UUID

abstract class TestBase extends AnyFreeSpec with Matchers:

  reactor.util.Loggers.useJdkLoggers()

