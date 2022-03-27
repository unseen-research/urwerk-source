package urwerk.source

import urwerk.test.TestBase

class ContextTest extends TestBase:
  "apply with tuples" in {
    val context = Context(("abc" -> "ABC"), ("def" -> "DEF"))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }

  "from map" in {
    val context = Context.from(Map(("abc" -> "ABC"), ("def" -> "DEF")))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }

  "from iterable" in {
    val context = Context.from(Seq(("abc" -> "ABC"), ("def" -> "DEF")))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }
