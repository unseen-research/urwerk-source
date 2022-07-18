package urwerk.app

import urwerk.test.TestBase

//import scala.util.TupledFunction

class CommandTest extends TestBase:

  val x: (String, Int, Boolean) => String = (a, b, c) => s"a: $a, b: $b, c: $c"

  "cmd" in {
    println(x("dfdf", 77, true))

    val y: ((String, Int, Boolean)) => String = x.tupled

    println( y(("y", 5, true)))
  }

  "tuple" in {
    val t = (1, 'a').map[[X] =>> Option[X]]([T] => (t: T) => Some(t))
    println(t)
  }

  case class Param[A]():
    def eval(value: A): String = s"Param: $value"

  //extension[T <: Tuple](tuple: T)
  def invoke[T <: Tuple, I <: Tuple.InverseMap[T, Param]](t: T, fn: I => Int): Int = 88

  //  //https://stackoverflow.com/questions/64339583/scala-3-extract-tuple-of-wrappers-and-inversemap-on-first-order-type

  "invoke" in {
    val f = (a: String, b: Int) => 3

    val tf: ((String, Int)) => Int = Function.tupled(f)

    val utf = Function.untupled(tf)

    invoke((Param[String](), Param[Int]()), tf)

    invoke((Param[String](), Param[Int]()), (a, b) => 77)

  }


//  extension [F, Args <: Tuple, R](f: Args => R) def untupled(using tf: TupledFunction[F, Args => R]): F = tf.untupled(f)
//
//  def xx[F[_]] = "???"
//
//  "xx" in {
//    xx[List]
//  }