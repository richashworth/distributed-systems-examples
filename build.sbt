name := "Distributed Systems Workshop Examples"

version := "1.3"

scalaVersion := "2.12.7"

cancelable in Global := true

libraryDependencies += "com.flyobjectspace" % "flyjava" % "2.0.4"

libraryDependencies += "io.nats" % "java-nats-streaming" % "2.1.0"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0-M1"
