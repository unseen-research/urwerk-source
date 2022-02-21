ThisBuild / organization := "ur.urwerk"
ThisBuild / version      := "0.1.2"

val DottyVersion = "3.1.1"
val ReactorVersion = "3.4.15"

githubOwner := "unseen-research"
githubRepository := "urwerk"
githubTokenSource := TokenSource.GitConfig("github.token")

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
    )
  )
