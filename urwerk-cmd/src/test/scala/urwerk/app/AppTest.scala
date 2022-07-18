package urwerk.app

import urwerk.test.TestBase
import cats.implicits.*
import com.monovore.decline.*
import cats.implicits.*

import scala.deriving.Mirror
import java.net.URI
import scala.concurrent.duration.Duration
import java.nio.file.Path
import scala.annotation.implicitNotFound

class AppTest extends TestBase:
  case class Abc(a: String, b: String)

  val abc = Abc("abc", "def")

  @implicitNotFound("${F} cannot be tupled as ${G}")
  sealed trait TupledFunction[F, G]:
    def tupled(f: F): G
    def untupled(g: G): F

  extension [F, Args <: Tuple, R](f: F)
    def tupled(using tf: TupledFunction[F, Args => R]): Args => R = tf.tupled(f)

  object TupledFunction:
//    extension [F, Args <: Tuple, R](f: F)
//      def tupled(using tf: TupledFunction[F, Args => R]): Args => R = tf.tupled(f)

    def apply[F, G](tupledImpl: F => G, untupledImpl: G => F): TupledFunction[F, G] =
        new TupledFunction[F, G]:
          def tupled(f: F): G = tupledImpl(f)
          def untupled(g: G): F = untupledImpl(g)


  "tuple func > 22" in {
    val f25 = (x1: Int, x2: Int, x3: Int, x4: Int, x5: Int, x6: Int, x7: Int, x8: Int, x9: Int, x10: Int, x11: Int, x12: Int, x13: Int, x14: Int, x15: Int, x16: Int, x17: Int, x18: Int, x19: Int, x20: Int, x21: Int, x22: Int, x23: Int, x24: Int, x25: Int) =>
      x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 + x12 + x13 + x14 + x15 + x16 + x17 + x18 + x19 + x20 + x21 + x22 + x23 + x24 + x25
    val t25 = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)

    //println(f25.tupled(t25))
  }


  "tuple func <= 22" in {
    val f22 = (x1: Int, x2: Int, x3: Int, x4: Int, x5: Int, x6: Int, x7: Int, x8: Int, x9: Int, x10: Int, x11: Int, x12: Int, x13: Int, x14: Int, x15: Int, x16: Int, x17: Int, x18: Int, x19: Int, x20: Int, x21: Int, x22: Int) =>
      x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 + x12 + x13 + x14 + x15 + x16 + x17 + x18 + x19 + x20 + x21 + x22

    val y = f22.tupled
  }



  "tuple" in {
    println(Tuple.fromProductTyped(abc))

    case class Dto(id:Int, json:String)

    val aCaseClass = Dto(1,"""{"key":3}""")
    val asATuple: (Int, String) = Tuple.fromProductTyped(aCaseClass)
    // and then the reverse
    val caseAgain: Dto = summon[Mirror.Of[Dto]].fromProduct(asATuple)
    println(s"[$aCaseClass] [$asATuple] [$caseAgain]")
  }
  "constituent example" in {
    type ConstituentPartOf[T] = T match
      case Boolean => Char
      case BigInt => Int
      case String => Char
      case List[t] => t

    val aNumber: ConstituentPartOf[BigInt] = 2
    val aCharacter: ConstituentPartOf[String] = 'a'
    val anElement: ConstituentPartOf[List[String]] = "Scala"

    def lastComponentOf[T](thing: T): ConstituentPartOf[T] = thing match
    case bb: Boolean => bb.toString.head
    case b: BigInt => (b % 10).toInt
    case s: String =>
      if (s.isEmpty) throw new NoSuchElementException
      else s.charAt(s.length - 1)
    case l: List[_] =>
      if (l.isEmpty) throw new NoSuchElementException
      else l.last

    val lastDigit: Int = lastComponentOf(BigInt(53)) // 3
    val lastChar: Char = lastComponentOf("Scala") // 'a'
    val lastElement: String = lastComponentOf((1 to 10).toList.map(_.toString)) // 10
    val lastbool = lastComponentOf(true)

    println(s"Const $lastDigit $lastChar $lastElement $lastbool")
  }


  type LeafElem[X] = X match
    case String => Char
    case Array[t] => LeafElem[t]
    case Iterable[t] => LeafElem[t]
    case AnyVal => X

  "match type" in {
    def leafElem[X](x: X): LeafElem[X] = x match
      case x: String      => x.charAt(0)
      case x: Array[t]    => leafElem(x(0))
      case x: Iterable[t] => leafElem(x.head)
      case x: AnyVal      => x

    val char: LeafElem[Char] = leafElem("abc")
    val int: LeafElem[Int] = leafElem("abc")
    val array = leafElem(Array("abc"))
    val seq = leafElem(Seq("abc"))
    val any = leafElem(Seq(5))

    println(s"ANY $char $int $array $seq $any")
  }

  "mapN" in {
    case class Config(name: String, value: String)

    val b = (Seq("abc", "def"), Seq("None")).mapN(Config)

    println(b)
  }

  "decline" in {

    val uriOpt = Opts.option[URI]("input-uri", "Location of the remote file.")
    // uriOpt: Opts[URI] = Opts(--input-uri <uri>)
    val timeoutOpt =
      Opts.option[Duration]("timeout", "Timeout for fetching the remote file.")
        .withDefault(Duration.Inf)
    // timeoutOpt: Opts[Duration] = Opts([--timeout <duration>])
    val fileOpt = Opts.option[Path]("input-file", "Local path to input file.")
    // fileOpt: Opts[Path] = Opts(--input-file <path>)
    val outputOpt = Opts.argument[Path]("output-file")
    // outputOpt: Opts[Path] = Opts(<output-file>)


    sealed trait InputConfig
    case class RemoteConfig(uri: URI, timeout: Duration) extends InputConfig
    case class LocalConfig(file: Path) extends InputConfig

    val remoteOpts = (uriOpt, timeoutOpt).mapN(RemoteConfig.apply)
    // remoteOpts: Opts[RemoteConfig] = Opts(--uri <uri> [--timeout <duration>])
    val localOpts = fileOpt.map(LocalConfig.apply)
    // localOpts: Opts[LocalConfig] = Opts(--input-file <path>)
    val inputOpts = remoteOpts orElse localOpts
    // inputOpts: Opts[Product with Serializable with InputConfig] = Opts(--uri <uri> [--timeout <duration>] | --input-file <path>)

    case class Config(
      input: InputConfig,
      queries: Path,
    )

    uriOpt.map(x=> x)

    val configOpts: Opts[Config] = (inputOpts, outputOpt).mapN(Config.apply)
  }