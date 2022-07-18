package urwerk.cmdln

import urwerk.test.TestBase

import Parameters.*

class ParameterTest extends TestBase:
  "parameters" in {
    val parameters = for
      a <- param[String]("param-a")
      b <- param[Int]("param-b")
      c <- param[Int]
    yield
      s"$a $b $c"
  }
