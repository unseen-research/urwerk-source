package urwerk.test

import java.io.OutputStream
import java.nio.file.{Files, Paths}
import java.util.UUID

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Codec
import scala.language.implicitConversions

import urwerk.io
import urwerk.io.file
import urwerk.io.file.given

abstract class TestBase extends AnyFreeSpec with Matchers:

  reactor.util.Loggers.useJdkLoggers()
