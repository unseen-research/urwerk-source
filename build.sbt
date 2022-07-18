ThisBuild / organization := "io.github.unseen-research.urwerk"
ThisBuild / version      := "0.1.2-SNAPSHOT"

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

val DottyVersion = "3.1.3"
val ReactorVersion = "3.4.15"

lazy val commonDependencies = Seq(
  "com.outr" %% "scribe" % "3.6.2",
  "com.outr" %% "scribe-slf4j" % "3.6.2",

  "com.monovore" %% "decline" % "2.2.0",

  "org.typelevel" %% "cats-core" % "2.7.0",

  "co.fs2" %% "fs2-core" % "3.2.7",
  "co.fs2" %% "fs2-io" % "3.2.7",

  "org.http4s" %% "http4s-client" % "1.0.0-M32",
  "org.http4s" %% "http4s-ember-client" % "1.0.0-M32",
  "org.http4s" %% "http4s-async-http-client" % "1.0.0-M32",

  "org.scalatest" %% "scalatest" % "3.2.11" % "test"
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature"
)

// val publishRepositoryUrl = if(version.endsWith("SNAPSHOT")) 
//     "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//   else
//     "https://s01.oss.sonatype.org/content/repositories/releases/"

val publishRepositoryUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
publishMavenStyle := true

lazy val root = project
  .in(file("."))
  .aggregate(
    urwerkCmd, urwerkHttp, urwerkIo, urwerkSource, urwerkSourceTest)
  .settings(
    name := "urwerk-source-root",
    description := "Urwerk - reactive library",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    publish / skip := true,
    publishLocal / skip := true,
  )

lazy val urwerkCmd = project
  .in(file("urwerk-cmd"))
  .settings(
    name := "urwerk-cmd",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    libraryDependencies ++= commonDependencies,
    
    publishTo :=  Some("nexus" at publishRepositoryUrl)
  )

lazy val urwerkIo = project
  .in(file("urwerk-io"))
  .settings(
    name := "urwerk-io",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    libraryDependencies ++= commonDependencies,
    
    publishTo :=  Some("nexus" at publishRepositoryUrl)
  )
  .dependsOn(
    urwerkSource,
    urwerkSourceTest % "test"
  )

lazy val urwerkHttp = project
  .in(file("urwerk-http"))
  .settings(
    name := "urwerk-http",

    scalaVersion := DottyVersion,
    scalacOptions ++= commonScalacOptions,

    libraryDependencies ++= commonDependencies ++ Seq(
      "com.github.tomakehurst" % "wiremock-jre8" % "2.32.0" % "test"
    ),
    
    publishTo :=  Some("nexus" at publishRepositoryUrl)
  )
  .dependsOn(
    urwerkIo,
    urwerkSource,
    urwerkSourceTest % "test"
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
    
    publishTo :=  Some("nexus" at publishRepositoryUrl)
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
    publishTo :=  Some("nexus" at publishRepositoryUrl)
  )
  .dependsOn(
    urwerkSource
  )

