val DottyVersion = "3.1.1"
val ReactorVersion = "3.4.15"

githubTokenSource := TokenSource.GitConfig("github.token")
githubOwner := "unseen-research"
githubRepository := "urwerk-source"

lazy val root = project
  .in(file("."))
  .settings(
    name := "urwerk-source",
    description := "Urwerk - reactive library",

    scalaVersion := DottyVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature"
    ),

    libraryDependencies ++= Seq(
      "io.projectreactor" % "reactor-core" % ReactorVersion % "compile",
      "io.projectreactor" % "reactor-test" % ReactorVersion % "test",
      "com.outr" %% "scribe" % "3.6.2",
      "com.outr" %% "scribe-slf4j" % "3.6.2",

      "org.scalatest" %% "scalatest" % "3.2.11" % "test"
      //"org.scalatestplus" %% "junit-4-13" % "3.2.9.0" % "test",
      //"com.novocode" % "junit-interface" % "0.11" % "test",
      //"junit" % "junit" % "4.13" % "test",
      //"com.github.tomakehurst" % "wiremock-jre8" % "2.27.2" % "test"
    )
  )
