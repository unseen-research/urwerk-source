package urwerk.app

class App(commands: Command[?]*):
  def main(args: Array[String]): Unit = ???

object Parameters:
  def param[A](name: String, names: String*): Parameters[A] = ???

  def param[A]: Parameters[A] = ???

  val initParam = Parameters[Unit](Seq("INIT"))

class Parameters[+A](names: Seq[String]):

  def name: String = names.headOption.getOrElse("")

  def value: A = ???

  def description(text: String): Parameters[A] =
    println(s"describe some names $names")
    this

  def flatMap[B](fn: A => Parameters[B])(using outer: Parameters[?]): Parameters[B] =
    println(s"FLAT MAP $names")
    val innerparam: Parameters[B] = fn(value)
    innerparam

  def map[B](fn: A => B)(using outer: Parameters[?]): Parameters[B] = ???

object Command:
  def apply[A](initParams: Parameters[?] ?=> Parameters[A]): Command[A] =

    val params = initParams(using Parameters.initParam)

    Command(params)


class Command[+A](parameters: Parameters[A]):
  def action[A1 >: A](action: A => Int | Unit): Command[A1] = ???
  def apply(args: Seq[String]): Int = ???

object Action:
  def apply(action: => Unit | Int): Unit | Int = action



val c = Command.apply {
  for{
    x <- Parameters.param[String]("x")
    y <- Parameters.param[String]("y")
    z <- Parameters.param[String]("z")
  }
  yield{
    "abc"
  }
}



//val Run = Command(
//  for
//    x <- param[String]("workspace", "w")
//      .description("workspace parqameter").default("")
//    y <- param[Boolean]("verbose", "v")
//      .description("workspace parqameter").default("")
//
//  yield
//    val config = Config(x, y)
//    Exec(
//
//      )
//      exe
//
//    )
//)


//object Main extends App(
//  Command(help)(
//    for
//      r <- param[String](r("ljdflaj"))
//      x <- param[String]("workspace", "w")
//        .description("workspace parqameter").default("")
//        y <- param[Boolean]("verbose", "v")
//          .description("workspace parqameter").default("")
//      tailParams
//
//    yield
//      (x, y)
//  ).action((x, y => run),
//    Command(
//
//    )
//
//)

//  val run = Command(
//    for
//      _ <- Seq(1)
//    yield
//      print("help")
//   ).execute{x =>
//
//    }


  //app run-app --abc abc --def def
//  def `run-app` =
//    for
//      abc <- Seq("abc")
//      defx <- Seq("def")
//      xyz <- Seq("xyz")
//    yield
//      Config(abc, defx, xyz)) -> (x) =>
//        println(x.abc)

