organization := "com.ubiregi"

name := "ubiregi_api_client_scala"

version := "0.1.0"

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.7",
  "net.databinder" %% "dispatch-json" % "0.8.7",
  "net.liftweb" %% "lift-json" % "2.4"
)

scalacOptions ++= Seq("-deprecation","-unchecked")
 
initialCommands in console += {
  Iterator("net.liftweb.json._").map("import "+).mkString("\n")
}
