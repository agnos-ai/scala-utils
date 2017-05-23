import sbt._
import Keys._

import scala.util.Try


object BuildSettings {
  val buildOrganization = "com.modelfabric"
  val buildScalaVersion = Version.scala
  val buildExportJars   = true

  val buildSettings = Seq (
    organization  := buildOrganization,
    scalaVersion  := buildScalaVersion,
    exportJars    := buildExportJars,
    shellPrompt := { state => "sbt [%s]> ".format(Project.extract(state).currentProject.id) },
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-target:jvm-1.8", "-language:implicitConversions", "-language:postfixOps", "-Xlint", "-Xfatal-warnings"),
    incOptions    := incOptions.value.withNameHashing(nameHashing     = true),
    ivyScala      := ivyScala.value map { _.copy(overrideScalaVersion = true) },
    parallelExecution in Test := false
  ) ++ Defaults.itSettings //++ Revolver.settings
}

object PublishingSettings {

  lazy val jenkinsMavenSettings = Seq(
    publishMavenStyle       := true,
    publishArtifact in Test := false,
    pomIncludeRepository    := { _ => false },
    publishTo := {
      if (isSnapshot.value) {
        Try("snapshots" at sys.env("REPOSITORY_SNAPSHOTS")).toOption
      } else {
        Try("releases" at sys.env("REPOSITORY_RELEASES")).toOption
      }
    },
    pomExtra := {
      <scm>
        <url>https://github.com/modelfabric/scala-utils.git</url>
        <connection>scm:git:https://github.com/modelfabric/scala-utils.git</connection>
      </scm>
        <developers>
          <developer>
            <id>jgeluk</id>
            <name>Jacobus Geluk</name>
            <url>https://github.com/orgs/modelfabric/people/jgeluk</url>
          </developer>
          <developer>
            <id>JianChen123</id>
            <name>Jian Chen</name>
            <url>https://github.com/orgs/modelfabric/people/JianChen123</url>
          </developer>
          <developer>
            <id>szaniszlo</id>
            <name>Stefan Szaniszlo</name>
            <url>https://github.com/orgs/modelfabric/people/szaniszlo</url>
          </developer>
        </developers>
    }
  )
}

object Version {

  val scala      = "2.11.8"
  val akka       = "2.5.1"
  val spray      = "1.3.3"
  val commnsLang = "3.4"
  val scalaTest  = "3.0.1"
}


object Library {

  val akkaActor         = "com.typesafe.akka"  %% "akka-actor"                        % Version.akka
  val akkaCluster       = "com.typesafe.akka"  %% "akka-cluster"                      % Version.akka
  val apacheCommons     = "org.apache.commons" %  "commons-lang3"                     % Version.commnsLang
  val scalaTest         = "org.scalatest"      %% "scalatest"                         % Version.scalaTest   % "it,test"
  val akkaTestkit       = "com.typesafe.akka"  %% "akka-testkit"                      % Version.akka        % "it,test"
}

object Build extends sbt.Build {

  import BuildSettings._
  import PublishingSettings._
  import Library._
  import plugins._

  val projectDependencies = Seq(
    akkaActor, akkaCluster, apacheCommons,
    scalaTest, akkaTestkit)

  lazy val project = Project("scala-utils", file("."))
    .configs(IntegrationTest)
    .settings(buildSettings: _*)
    .settings(jenkinsMavenSettings: _*)
    .settings(name := "scala-utils")
    .settings(libraryDependencies ++= projectDependencies)
} 
