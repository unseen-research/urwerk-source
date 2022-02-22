ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

sonatypeProfileName := "io.github.unseen-research"

publishMavenStyle := true

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

publishTo := sonatypePublishToBundle.value