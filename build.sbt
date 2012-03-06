organization := "com.ubiregi"

name := "ubiregi_api_client_scala"

version := "0.1.0"

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.7",
  "net.databinder" %% "dispatch-json" % "0.8.7",
  "net.databinder" %% "dispatch-gae" % "0.8.7",
  "net.liftweb" %% "lift-json" % "2.4"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.8.2" % "test"
  // with Scala 2.8.x (specs2 1.5 is the latest version for scala 2.8.x)
  // "org.specs2" %% "specs2" % "1.5" % "test",
  // "org.specs2" %% "specs2-scalaz-core" % "5.1-SNAPSHOT" % "test"
)
  
// Read here for optional dependencies: 
// http://etorreborre.github.com/specs2/guide/org.specs2.guide.Runners.html#Dependencies
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots", "releases"  at "http://oss.sonatype.org/content/repositories/releases")

scalacOptions ++= Seq("-deprecation","-unchecked")
 
initialCommands in console += {
  Iterator("net.liftweb.json._").map("import "+).mkString("\n")
}
