package urwerk.test

import cats.data.IndexedStateT
import fs2.*
import cats.effect.unsafe.implicits.global
import org.http4s.ember.client.*
import org.http4s.client.*
import org.http4s.*
import org.http4s.Request
import cats.effect.*
import org.http4s.syntax.all.uri
//import org.http4s.implicits._
//import com.comcast.ip4s._
//import org.http4s._
//import org.http4s.dsl.io._
//import org.http4s.implicits._
//import org.http4s.ember.server._
//import org.http4s.server.middleware.Logger
//import scala.concurrent.duration._

class StreamTest extends TestBase:

  "stream" in {
    val res = Stream(1, 2, 3)
      .map(_.toString)
      .compile.drain.toString

    println(s"RES $res")
  }

  "client" in {
//    import java.util.concurrent._
//
//    val blockingPool = Executors.newFixedThreadPool(5)
//    val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

    EmberClientBuilder.default[IO].build.use { client =>
      val request = Request.apply[IO](Method.GET, uri"https://repo.maven.apache.org/maven2/com/typesafe/play/play_2.13/2.7.3/play_2.13-2.7.3.jar")
      val response: Stream[IO, Response[IO]] = client.stream(request)


      val io: IO[Unit] = response
        .evalTap(r=> IO(println(s"STATUS ${r.status}")))
        .flatMap{r =>
          r.body}
        .compile.drain

      io
    }.unsafeRunSync()
  }

  "writer monade" in {
    import cats.data.Writer
    import cats.instances.vector._

    val x = Writer(Seq("some intermediary computation"), 3)
    val y = Writer(Seq("another intermediary computation"), 4)

    val z = for {
      a <- x
      b <- y
    } yield a + b

    println(s"VALUE ${z.value}")
    println(s"Container ${z.written}")
  }

  "reader monade" in {
    case class Config(name: String, age: Int)

    import cats.data.Reader
    type ConfigReader[A] = Reader[Config, A]

    def greet(salutation: String): ConfigReader[String] = Reader(cfg => s"$salutation ${cfg.name}")

    def validAge: ConfigReader[Int] = Reader(cfg => math.abs(cfg.age))

    import cats.syntax.applicative._ // allows us to use `pure`

    def greeting: ConfigReader[String] = for {
        g <- greet("Hi")
        a <- validAge
        p <- (if (a < 18) "a child" else "an adult").pure[ConfigReader]
    } yield s"$g; you are $p."


    val myCfg = Config("Holmes", -37)

    println(greeting.run(myCfg))
  }

  "state monade" in {
    import cats.data.State

    def addOne = State[Int, String] { state =>
      val a = state + 1
      (a, s"Result of addOne is $a")
    }

    def double = State[Int, String] { state =>
      val a = state * 2
      (a, s"Result of double is $a")
    }

    def modTen = State[Int, String] { state =>
      val a = state % 10
      (a, s"Result of modTen is $a")
    }

//    val aaa: IndexedStateT[IO, String, String, String] = ???
//
    //val xxx = addOne.flatMap(a=> ??? ).run(4)
    val (state0: Int, result0: String) = addOne.flatMap{ a =>
      double.flatMap(b =>
        modTen.map(c =>
          c
        )
      )
    }.run(4).value

    println(s"STATE1 $state0")
    println(s"RES1 $result0")

    def genNumber = for {
        a <- addOne // threads the new state to the next computation
        b <- double // threads the new state to the next computation
        c <- modTen
    } yield c

    val (state: Int, result: String) = genNumber.run(3).value

    println(s"STATE1 $state")
    println(s"RES1 $result")

    val resultOnly = genNumber.runA(3)
    println(s"RES2 $resultOnly")

    val stateOnly  = genNumber.runS(3)
    println(s"STATE2 $stateOnly")

  }