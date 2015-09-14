package com.modelfabric.akka.config

import com.typesafe.config.{Config, ConfigFactory => AkkaConfigFactory}
import com.modelfabric.akka.cluster.SeedNodeInfo

private class ConfigFactory private (params: ConfigParams) {

  private def firstPass() = {

    val regularConfig = AkkaConfigFactory.load()
    val joinCluster = ! regularConfig.getList("akka.cluster.seed-nodes").isEmpty
    val launchParams =
    s"""
      | launch {
      |   verbose = ${params.verbose.getOrElse(false).toString()}
      |   debug = ${params.debug.getOrElse(false).toString()}
      |   test = ${params.test.getOrElse(false).toString()}
      |   seed-node-index = ${params.seedNodeIndex.getOrElse(0).toString()}
      |   join-cluster = $joinCluster
      | }
    """.stripMargin
    val logLevel = if (params.isVerbose) "INFO" else if (params.isDebug) "DEBUG" else "ERROR"
    val logLevelConfigLines = s"""akka.loglevel="$logLevel" """
    val testConfigLines =
    """
      | # how often should the node send out gossip information?
      | akka.cluster.gossip-interval = 530s
    """.stripMargin
    val additionalConfigLines = launchParams + logLevelConfigLines + (if (params.isTest) testConfigLines else "")
    val myConfig = AkkaConfigFactory.parseString(additionalConfigLines)

    myConfig.withFallback(regularConfig)
  }

  /*
   * Before we create the Akka System instance, we need to override the configuration of the port on which
   * this Akka Node listens. If the seedNodeIndex > 0, then we are a Seed Node.
   */
  private def secondPass(seedNodeIndex: Int, config: Config) : Config = SeedNodeInfo(config)

  def factory = AkkaConfigFactory.load(secondPass(params.getSeedNodeIndex, firstPass()))
}

case class ConfigParams(
  verbose: Option[Boolean] = Some(false),
  debug: Option[Boolean] = Some(false),
  test: Option[Boolean] = Some(false),
  seedNodeIndex: Option[Int] = None
) {

  def isVerbose = verbose.getOrElse(false)
  def isDebug = debug.getOrElse(false)
  def isTest = test.getOrElse(false)
  def getSeedNodeIndex = seedNodeIndex.getOrElse(0)
}

/**
 * Deals with the Akka application.conf file
 */
object ConfigFactory {

  /**
   * Load the Akka Config and modify the loglevel variable depending on the various flags in AkkaConfigParams that can
   * overrule or influence the content of application.conf
   */
  def apply(params: ConfigParams) : Config = new ConfigFactory(params).factory
}
