organization := "com.ubiregi"

name := "ubiregi_api_scala"

version := "0.1.0"

scalaVersion := "2.9.2"

seq(assemblySettings: _*)

libraryDependencies ++= {
val dispatchVersion = "0.8.9"
Seq(
  "net.databinder" %% "dispatch-http" % dispatchVersion,
  "net.databinder" %% "dispatch-json" % dispatchVersion,
  "net.liftweb" % "lift-json_2.9.1" % "2.4"
)
}

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.12.3" % "test"
)

scalacOptions ++= Seq("-deprecation","-unchecked")

initialCommands in console += {
  Iterator("net.liftweb.json._").map("import "+).mkString("\n")
}
