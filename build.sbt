name := "scala-scapi"

version := "1.0.0"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1"
)