package urwerk.cli

import urwerk.cli.ParameterList.{Label, Setting}
import urwerk.cli.Parameter.{param, trailingArgs}
import urwerk.test.TestBase

class ParameterListTest extends TestBase:

  "collect positional value arg" - {
    val params = ParameterList[Seq[Int]](
      param[Int]
        .apply{(config, value) => config :+ value}
    )

    "followed by nothing" in {
      params.collect(Seq(), Seq("77")) should be ((Seq(77), Position(1, 0)))
    }

    "negative number value starting with minus which is flags prefix" in {
      params.collect(Seq(), Seq("-77")) should be ((Seq(-77), Position(1, 0)))
    }

    "followed by value" in {
      params.collect(Seq(), Seq("77", "88")) should be ((Seq(77), Position(1, 0)))
    }

    "followed by name" in {
      params.collect(Seq(), Seq("77", "88")) should be ((Seq(77), Position(1, 0)))
    }

    "followed by flags" in {
      params.collect(Seq(), Seq("77", "-flags")) should be ((Seq(77), Position(1, 0)))
    }
  }

  "collect named param" - {
    val params = ParameterList[Seq[String]]{
        param[String]("param", "alias", "p")
          .apply{(config, value) => config :+ value}} 

    "with primary name followed by nothing" in {
      params.collect(Seq(), Seq("--param", "any-value")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with primary name followed by value" in {
      params.collect(Seq(), Seq("--param", "any-value", "other")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with primary name followed by name" in {
      params.collect(Seq(), Seq("--param", "any-value", "--other")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with primary name followed by flags" in {
      params.collect(Seq(), Seq("--param", "any-value", "-other")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with alias name" in {
      params.collect(Seq(), Seq("--alias", "any-value", "--other")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with short name" in {
      params.collect(Seq(), Seq("-p", "any-value", "--other")) should be ((Seq("any-value"), Position(2, 0)))
    }

    "with single quoted value" in {
      params.collect(Seq(), Seq("--alias", "'--any-value--'")) should be ((Seq("--any-value--"), Position(2, 0)))
    }

    "with double quoted value" in {
      params.collect(Seq(), Seq("--alias", "\"--any-value--\"")) should be ((Seq("--any-value--"), Position(2, 0)))
    }

  }

  "collect named boolean param" - {
    val params = ParameterList[Set[String]]{
        param[Boolean]("param", "alias")
          .apply{(config, value) => config + s"param-$value"}} 

    "with explicit true value" in {
      params.collect(Set(), Seq("--param", "true")) should be ((Set("param-true"), Position(2, 0)))
    }

    "with explicit false value" in {
      params.collect(Set(), Seq("--param", "false")) should be ((Set("param-false"), Position(2, 0)))
    }

    "without value defaults to implicit true value" in {
      params.collect(Set(), Seq("--param")) should be ((Set("param-true"), Position(1, 0)))
    }

    "followed by name" in {
      params.collect(Set(), Seq("--param", "--any-value")) should be ((Set("param-true"), Position(1, 0)))
    }

    "followed by none boolean value defaults to implicit true value" in {
      params.collect(Set(), Seq("--param", "any")) should be ((Set("param-true"), Position(1, 0)))
    }

    "followed by separator followed by boolean defaults to implicit true value" in {
      val params2 = params.set(
        param[Boolean]
          .apply{(config, value) => config + s"positional-$value"})
      
      params2.collect(Set(), Seq("--param", "--", "false")) should be ((Set("param-true", "positional-false"), Position(3, 0)))
    }
  }

  "joined flags" - {
    val params = ParameterList[Set[String]](
      param[Boolean]("a-param", "a")
        .apply{(config, value) => config + s"a-$value"},
      param[Boolean]("b-param", "b")
        .apply{(config, value) => config + s"b-$value"},
      param[Boolean]("c-param", "c")
        .apply{(config, value) => config + s"c-$value"},
      param[String]("d-param", "d")
        .apply{(config, value) => config + s"d-$value"})

    "without value" in {
      params.collect(Set(), Seq("-abc")) should be ((Set("a-true", "b-true", "c-true"), Position(1, 0)))
    }

    "with value for last flag" in {
      params.collect(Set(), Seq("-abcd", "any-value")) should be ((Set("a-true", "b-true", "c-true", "d-any-value"), Position(2, 0)))
    }

    "with segregated value after last flag" in {
      params.collect(Set(), Seq("-abc", "any-value")) should be ((Set("a-true", "b-true", "c-true"), Position(1, 0)))
    }

    "stop within flags field" in {
      params.collect(Set(), Seq("-abcXY")) should be ((Set("a-true", "b-true", "c-true"), Position(0, 3)))
    }
  }

  "positional values" in {
    val params = ParameterList[Seq[Int]](
        param[Int]
          .apply{(config, value) => config :+ value},
        param[Int]
          .apply{(config, value) => config :+ value})
      .set(
        param[Int]
          .apply{(config, value) => config :+ value},
        param[Int]
          .apply{(config, value) => config :+ value})

    params.collect(Seq(), Seq("1", "2", "3", "4", "5")) should be (Seq(1, 2, 3, 4), Position(4, 0))
  }

  "required named param not found" in {
    val params = ParameterList[Seq[Int]](
      param[Int]("number")
        .required)

    val ex = intercept[ParameterNotFoundException]{
      params.collect(Seq(), Seq())
    }
  
    ex.position should be (Position(0, 0))
    ex.param.name should be ("number")
  }

  "required positional param not found" in {
    val params = ParameterList[Seq[Int]](
      param[Int]
        .label("a")
        .apply{(config, value) => config :+ value},
      param[Int]
        .label("b")
        .required,
      param[Int]
        .label("c")        
        .apply{(config, value) => config :+ value})

    val ex = intercept[ParameterNotFoundException]{
      params.collect(Seq(), Seq("1"))
    }

    ex.position should be (Position(1, 0))
    ex.param.label should be ("b")
  }

  "multiple params" in {
    val params = ParameterList[Set[String]](
      param[Boolean]("param1", "a")
        .apply{(config, value) => config + s"param1-$value"},
      param[Int]("param2", "b")
        .apply{(config, value) => config + s"param2-$value"},
      param[Int]
        .apply{(config, value) => config + s"pos-int-$value"},
      param[String]("param3", "c")
        .apply{(config, value) => config + s"param3-$value"},
      param[String]
        .apply{(config, value) => config + s"pos-string-$value"}, 
      param[Boolean]
        .apply{(config, value) => config + s"pos-boolean-$value"})

    val (config, pos) = params.collect(Set(), Seq("--param1", "true", "77", "--param1", "false", "positional-value", "true", "--param3", "value3", "--param2", "42", "tail1", "tail2"))
    
    config should be(Set("pos-boolean-true", "param1-false", "pos-int-77", "param3-value3", "param1-true", "pos-string-positional-value", "param2-42"))
    pos should be (Position(11, 0))
  }

  "collect with initial position" in {
    val params = ParameterList[Seq[Int]](
      param[Int]
        .apply{(config, value) => config :+ value})

    val (config, pos) = params.collect(Seq(), Position(2, 0), Seq("abc", "def", "77", "xyz"))
    config should be (Seq(77))
    pos should be (Position(3, 0))
  }

  "reject named parameter value" in {
    val params = ParameterList[Seq[Int]](
      param[Int]("param")
        .accept(_ => false)
        .apply{(config, value) => config :+ value})

    val exception = intercept[ParameterValueRejected]{
      params.collect(Seq(), Seq("--param", "value"))}

    exception.position should be (Position(1, 0))
  }

  "reject positional parameter value" in {
    val params = ParameterList[Seq[Int]](
      param[Int]
        .accept(_ => true)
        .apply{(config, value) => config :+ value},
      param[Int]
        .accept(_ => false)
        .apply{(config, value) => config :+ value})

    val exception = intercept[ParameterValueRejected]{
      params.collect(Seq(), Seq("1", "2", "3"))}

    exception.position should be (Position(1, 0))
  }

  "trailing args" in {
    val params = ParameterList[String](
      trailingArgs{(config, value) => 
        config + value
      })
    val (config, pos) = params.collect("", Seq("a", "b", "c"))
    config should be ("abc")
    pos should be (Position(3, 0))
  }

  "params with trailing args" in {
    val params = ParameterList[String](
      param[Int]{(config, value) => 
        config + value},
      trailingArgs{(config, value) => 
        config + value
      })

      val (config, pos) = params.collect("", Seq("1", "a", "b", "c"))
      config should be ("1abc")
      pos should be (Position(4, 0))
  }

  "from settings" - {
    given WithConfig[String] with {}
 
    val namedParams = Seq(
      param[Boolean]("param1", "a"),
      param[Int]("param2", "b"))

    val positionalParams = Seq(
      param[Boolean],
      param[Int])

    val settings = namedParams ++ positionalParams
    
    "named params" in {
      val params = ParameterList.from(settings)
      params.namedParams should be (namedParams)
    } 

    "positional params" in {
      val params = ParameterList.from(settings)
      params.positionalParams should be (positionalParams)
    } 

    "without label" in {
      val params = ParameterList.from(settings)
      params.label should be("")
    }

    "with label" in {
      val params = ParameterList.from(settings :+ Label("LABEL"))
      params.label should be("LABEL")
    }

    "without trailing args" in {
      val params = ParameterList.from(settings)
      params.trailingArgs should be (None)
    }

    "with trailing args" in {
      val givenTrailingArgs = trailingArgs
      val params = ParameterList.from(settings :+ givenTrailingArgs)
      
      params.trailingArgs should be (Some(givenTrailingArgs))
    }
  }
