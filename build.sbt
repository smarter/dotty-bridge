lazy val root = (project in file(".")).
  settings(
    organization := "ch.epfl.lamp",
    name := "dotty-bridge",
    description := "sbt compiler bridge for Dotty",
    libraryDependencies := Seq(
      "org.scala-lang" %% "dotty" % "0.1-SNAPSHOT",
      "org.scala-sbt" % "interface" % "0.13.10-SNAPSHOT"
    ),
    version := "0.1-SNAPSHOT",
    // 2.11.5 is the version used by Dotty itself currently, we do the same to
    // avoid trouble.
    scalaVersion := "2.11.5",
    // Ideally, the sources should be published with crossPaths := false and the
    // binaries with crossPaths := true, but I haven't figured out how to do this.
    crossPaths := false
  )
