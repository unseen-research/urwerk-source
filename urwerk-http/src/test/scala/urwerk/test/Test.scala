package urwerk.test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

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

private val _httpServer = new WireMockServer(options().port(8089))

def httpServer: WireMockServer =
  _httpServer.start()
  sys.addShutdownHook{
    _httpServer.stop()}
  _httpServer

// def uniqueString: String = UUID.randomUUID().toString

// def uniquePath: file.Path = file.Path(s"build/tests/$uniqueString")

// def uniqueDirectory: file.Path = Files.createDirectories(uniquePath)

// def uniqueFile: file.Path =
//   val path = uniquePath
//   Files.createDirectories(path.getParent)
//   Files.createFile(path)

// def uniqueFile(bytes: Array[Byte]): file.Path =
//   Files.write(uniqueFile, bytes)

// def uniqueFile(content: String)(using codec: Codec): file.Path =
//   Files.writeString(uniqueFile, content, codec.charSet)

