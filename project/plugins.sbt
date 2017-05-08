// Comment to get more information during initialization
logLevel := Level.Info

resolvers ++= Seq("spray repo" at "http://repo.spray.io")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")
