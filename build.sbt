organization := "com.ubiregi"

name := "ubiregi_api_client_scala"

version := "0.1.0"

scalaVersion := "2.9.2"

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.8",
  "net.databinder" %% "dispatch-json" % "0.8.8",
  "net.databinder" %% "dispatch-nio" % "0.8.8",
  "net.databinder" %% "dispatch-gae" % "0.8.8",
  "net.liftweb" % "lift-json_2.9.1" % "2.4"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.12.1" % "test"
)
  
scalacOptions ++= Seq("-deprecation","-unchecked")
 
initialCommands in console += {
  Iterator("net.liftweb.json._").map("import "+).mkString("\n")
}
