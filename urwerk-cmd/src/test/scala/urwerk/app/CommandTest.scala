package urwerk.app

import urwerk.test.TestBase

object Param:
  def apply[A]: Param[A] = ???

  def apply[A](name: String): Param[A] = ???

trait Param[A]

def command(name: String)(params: Param[?] *): CommandSpec = ???

trait CommandSpec:
  def name: String

  def onApply[A](op: A => Int): Command[A] with CommandSpec

trait Command[A]:
  def apply(): Int

  def onApply(op: A => Int): Command[A] with CommandSpec


case class AnyConfig(a: String, b: Int, c: Boolean)

class CommandTest extends TestBase:
  val x: (String, Int, Boolean) => String = (a, b, c) => s"a: $a, b: $b, c: $c"

  "cmd" in {

    val fn = (a: AnyConfig) => 7
    val cmd = command("run")(Param[String], Param[Int]("number")).onApply[AnyConfig]{config => 7}

    val cmd2 = command("run")(Param[String], Param[Int]("number")).onApply[AnyConfig](fn)

  }
