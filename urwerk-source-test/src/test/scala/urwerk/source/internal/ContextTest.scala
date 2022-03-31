package urwerk.source.internal

import urwerk.test.TestBase
import urwerk.source.internal.ContextAdapter
import urwerk.source.Context

import reactor.util.context.{Context => UnderlyingContext}

class ContextConverterTest extends TestBase:
  "from underlying" in {
    val context = ContextConverter.fromUnderlying(
      UnderlyingContext.of("abc", "ABC"))

    context.toMap should be (Map("abc" -> "ABC"))
  }

  "to underlying with wrapped context adapter" in {
    val givenUnderlyingContext = UnderlyingContext.of("abc", "ABC")

    val actualUnderlyingContext = ContextConverter.toUnderlying(
      ContextAdapter.wrap(givenUnderlyingContext))

    actualUnderlyingContext should be theSameInstanceAs givenUnderlyingContext
  }

class ContextAdapterTest extends TestBase:
  "apply with tuples" in {
    val context = ContextAdapter(("abc" -> "ABC"), ("def" -> "DEF"))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }

  "apply or else" in {
    val context = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC"))

    context.applyOrElse("abc", key => s"or else $key") should be ("ABC")
    context.applyOrElse("def", key => s"or else $key") should be ("or else def")
  }

  "empty" in {
    val context = ContextAdapter.empty
    context.size should be (0)
    context.isEmpty should be(true)
    context.iterator.toSeq should be (Seq())
    context.toSeq should be (Seq())
    context.toMap should be (Map())
  }

  "from map" in {
    val context = ContextAdapter.from(Map(("abc" -> "ABC"), ("def" -> "DEF")))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }

  "from iterable" in {
    val context = ContextAdapter.from(Seq(("abc" -> "ABC"), ("def" -> "DEF")))
    context.size should be (2)
    context("abc") should be ("ABC")
    context("def") should be ("DEF")
  }

  "get contained properties " in {
    val context: Context = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC", "def", "DEF"))
    context.contains("abc") should be (true)
    context.get(("abc")) should be (Some("ABC"))
    context("abc") should be ("ABC")

    context.contains(("def")) should be (true)
    context.get(("def")) should be (Some("DEF"))
    context("def") should be ("DEF")

    context.size should be (2)
  }

  "get not contained property" in {
    val context: Context = ContextAdapter.empty
    context.get("abc") should be (None)
  }

  "get not contained property throws NoSuchElementException" in {
    val context: Context = ContextAdapter.empty
    intercept[NoSuchElementException]{
      context("abc")}
  }

  "remove" in {
    val context: Context = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC", "def", "DEF"))
      .removed("abc")

    context.size should be (1)
    context.contains("abc") should be (false)
    context.contains("def") should be (true)
  }

  "to map" in {
    val map = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC", "def", "DEF")).toMap

    map should be (Map("abc" -> "ABC", "def" -> "DEF"))
  }

  "to seq" in {
    val seq = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC", "def", "DEF")).toSeq

    seq should be (Seq("abc" -> "ABC", "def" -> "DEF"))
  }

  "to set" in {
    val set = ContextAdapter.wrap(UnderlyingContext.of("abc", "ABC", "def", "DEF")).toSet

    set should be (Set("abc" -> "ABC", "def" -> "DEF"))
  }

  "updated" in {
    val context = ContextAdapter.empty.updated("abc", "ABC")
    context.size should be (1)
    context("abc") should be ("ABC")
  }