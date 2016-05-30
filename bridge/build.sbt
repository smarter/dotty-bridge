lazy val root = (project in file(".")).
  settings(
    organization := "ch.epfl.lamp",
    name := "dotty-bridge",
    description := "sbt compiler bridge for Dotty",
    resolvers += Resolver.typesafeIvyRepo("releases"),
    libraryDependencies := Seq(
      "org.scala-lang" %% "dotty" % "0.1-SNAPSHOT",
      "org.scala-sbt" % "interface" % sbtVersion.value,
      "org.scala-sbt" % "api" % sbtVersion.value % "test",
      "org.specs2" %% "specs2" % "2.3.11" % "test"
    ),
    publishArtifact in packageDoc := false,
    version := "0.1.1-SNAPSHOT",
    // 2.11.5 is the version used by Dotty itself currently, we do the same to
    // avoid trouble.
    scalaVersion := "2.11.5",
    // Ideally, the sources should be published with crossPaths := false and the
    // binaries with crossPaths := true, but I haven't figured out how to do this.
    crossPaths := false,

    fork in Test := true,
    parallelExecution in Test := false
  )

// Options for scripted tests
ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := Seq("-Xmx1024m")
scriptedBufferLog := false
// TODO: Use this instead of manually copying DottyInjectedPlugin.scala
// everywhere once https://github.com/sbt/sbt/issues/2601 gets fixed.
/*
scriptedPrescripted := { f =>
  IO.write(inj, """
import sbt._
import Keys._

object DottyInjectedPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  override val projectSettings = Seq(
    scalaVersion := "0.1-SNAPSHOT",
    scalacOptions += "-language:Scala2",
    scalaBinaryVersion  := "2.11",
    autoScalaLibrary := false,
    libraryDependencies ++= Seq("org.scala-lang" % "scala-library" % "2.11.5"),
    scalaCompilerBridgeSource := ("ch.epfl.lamp" % "dotty-bridge" % "0.1-SNAPSHOT" % "component").sources()
  )
}
""")
}
*/
