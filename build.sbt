ThisBuild / organization := "io.github.unseen-research"
ThisBuild / version      := "0.1.2-SNAPSHOT"

val DottyVersion = "3.1.1"
val ReactorVersion = "3.4.15"

lazy val commonDependencies = Seq(
  "com.outr" %% "scribe" % "3.6.2",
  "com.outr" %% "scribe-slf4j" % "3.6.2",

  "org.scalatest" %% "scalatest" % "3.2.11" % "test"
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature"
)

lazy val root = project
  .in(file("."))
  .aggregate(urwerkSource, urwerkSourceTest)
  .settings(
    name := "urwerk-source-root",
    description := "Urwerk - reactive library",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )

lazy val urwerkSource = project
  .in(file("urwerk-source"))
  .settings(
    name := "urwerk-source",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    libraryDependencies ++= commonDependencies ++ Seq(
      "io.projectreactor" % "reactor-core" % ReactorVersion % "compile",
      "io.projectreactor" % "reactor-test" % ReactorVersion % "test"
    ),

    publishTo :=  Some("nexus" at "https://s01.oss.sonatype.org/content/repositories/snapshots/")
  )

lazy val urwerkSourceTest = (project in file("test"))
  .in(file("urwerk-source-test"))
  .settings(
    name := "urwerk-source-test",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    libraryDependencies ++= commonDependencies ++ Seq(
      "io.projectreactor" % "reactor-test" % ReactorVersion % "compile"
    ),
    publishTo :=  Some("nexus" at "https://s01.oss.sonatype.org/content/repositories/snapshots/")
  )
  .dependsOn(
    urwerkSource
  )

