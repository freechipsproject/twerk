// See README.md for license details.

val org  = "edu.berkeley.cs"

name := "twerk"
version := "3.1.7"
scalaVersion := "2.12.8"
maxErrors := 5 
updateOptions := updateOptions.value.withLatestSnapshots(false)

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
val defaultVersions = Map(
  "firrtl" -> "1.2-SNAPSHOT",
  "chisel3" -> "3.2-SNAPSHOT",
  "treadle" -> "1.1-SNAPSHOT",
  "chisel-iotesters" -> "1.3-SNAPSHOT"
  )


libraryDependencies ++= Seq("firrtl","chisel3", "treadle", "chisel-iotesters").map {
  dep: String => org %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) }

// Linear aglebra 
libraryDependencies ++= Seq (
  "org.la4j" % "la4j" % "0.6.0",
  "org.scalaz" %% "scalaz-zio" % "1.0-RC4",
)

scalacOptions ++= Seq (
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xsource:2.11",
  "-language:reflectiveCalls",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)

// Lint
scapegoatVersion in ThisBuild := "1.3.8"
scalaBinaryVersion in ThisBuild := "2.12"
