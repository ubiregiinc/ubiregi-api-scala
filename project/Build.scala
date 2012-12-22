import sbt._,Keys._

object build extends Build {

  lazy val baseSettings = seq(
    organization := "com.ubiregi",
    version := "0.2.0-SNAPSHOT",
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq("-deprecation","-unchecked")
  )

  val dispatchVersion = "0.8.9"

  lazy val root:Project = Project(
    "root",
    file(".")
  ).settings(
    baseSettings ++ Seq(
      publishArtifact := false, publish := {}, publishLocal := {}
    ) : _*
  ).aggregate(
    core, example
  )

  lazy val core = Project(
    "core",
    file("core")
  ).settings(
    baseSettings ++ Seq(
      name := "ubiregi_api_scala",
      libraryDependencies ++= Seq(
        "net.databinder" %% "dispatch-http" % dispatchVersion,
        "net.databinder" %% "dispatch-json" % dispatchVersion,
        "net.liftweb" % "lift-json_2.9.1" % "2.4",
        "org.specs2" %% "specs2" % "1.12.3" % "test"
      ),
      initialCommands in console += {
        Iterator("net.liftweb.json._").map("import "+).mkString("\n")
      }
    ) : _*
  )

  lazy val example = Project(
    "example",
    file("example")
  ).settings(
    baseSettings ++ Seq(
      name := "ubiregi_api_scala_example",
      libraryDependencies ++= Seq(
        "com.github.kmizu" %% "jsonda" % "0.4.0"
      )
    ) : _*
  ).dependsOn(core)
}



