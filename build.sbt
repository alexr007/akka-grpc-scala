import sbt.Keys.scalaVersion

name := "akka-grpc-scala"

val commonSettings = Seq(
  scalaVersion := "2.13.4",
  libraryDependencies ++= Seq(
    akka %% "akka-http" % akkaHttpVersion,
    akka %% "akka-http2-support" % akkaHttpVersion,
    akka %% "akka-actor-typed" % akkaVersion,
    akka %% "akka-stream" % akkaVersion,
    akka %% "akka-discovery" % akkaVersion,
    akka %% "akka-pki" % akkaVersion,

    "ch.qos.logback" % "logback-classic" % "1.2.3",

    akka %% "akka-actor-testkit-typed" % akkaVersion % Test,
    akka %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.1.1" % Test
  ),
)

lazy val akkaVersion = "2.6.12"
lazy val akkaHttpVersion = "10.2.3"
lazy val akkaGrpcVersion = "1.1.0"
lazy val akka = "com.typesafe.akka"

val server_api = (project in file("server_api"))
  .enablePlugins(AkkaGrpcPlugin)
  .settings(scalaVersion := "2.13.4")

val server = (project in file("server"))
  .settings(commonSettings)
  .dependsOn(server_api)

val client = (project in file("client"))
  .settings(commonSettings)
  .dependsOn(server_api)
