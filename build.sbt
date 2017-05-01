import sbt._
import Process._
import Keys._

scalaVersion  := "2.11.8"

lazy val commonSettings = Seq(
  organization := "com.modelfabric",
  version      := "0.3-SNAPSHOT"
)



scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-target:jvm-1.8", "-language:implicitConversions", "-language:postfixOps", "-Xlint", "-Xfatal-warnings")

libraryDependencies ++= {
  val akkaVersion = "2.5.0"
  val spray       = "1.3.3"
  val commnsLang  = "3.4"
  val scalaTest   = "2.2.5"
  Seq(
    "com.typesafe.akka"  %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"  %% "akka-cluster"                      % akkaVersion,
    "org.apache.commons" %  "commons-lang3"                     % commnsLang,
    "io.spray"           %% "spray-can"                         % spray,
    "org.scalatest"      %% "scalatest"                         % scalaTest          % "test",
    "com.typesafe.akka"  %% "akka-testkit"                      % akkaVersion        % "test"
  )
}

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "scala-utils"
  )

fork in run := true