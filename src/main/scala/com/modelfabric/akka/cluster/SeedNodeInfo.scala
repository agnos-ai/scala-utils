package com.modelfabric.akka.cluster

import akka.event.LoggingAdapter
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}

/**
 * A SeedNodeInfo instance represents one SeedNode URL as defined in application.conf under akka.cluster.seed-nodes
 */
case class SeedNodeInfo private (seedNodeIndex : Int, url: String)

object SeedNodeInfo {

  import java.net._

  private lazy val hostIpAddress = {

    val localhost = InetAddress.getLocalHost

    localhost.getHostAddress
  }

  def seedNodeIndex(config: Config) = config.getInt("launch.seed-node-index")

  def seedNodesList(config: Config) : List[String] =
    config.getStringList("akka.cluster.seed-nodes").toList.map( (x: String) =>
      x.replaceAll("localhost", hostIpAddress).replaceAll("127.0.0.1", hostIpAddress)
    )

  /**
   * @param config the current application.conf config, assuming that it has a launch section with at least the
   *               seedNodeIndex variable set, where zero means that we're not running as a seed node.
   *
   * @return a copy of the given config with some changes: the netty tcp port will be set when we're running as a
   *         seed node and the hostname of the seed nodes will be altered if necessary.
   */
  def apply(config: Config) : Config = {

    val snIndex       = seedNodeIndex(config)
    val seedNodes     = seedNodesList(config)

    require(seedNodes.nonEmpty, "akka.cluster.seed-nodes has not been configured")
    require(snIndex <= seedNodes.size, "launch.seed-node-index is not <= number of configured seed-nodes")

    def nettyPortLine = if (snIndex > 0) {

      val seedNode      = seedNodes(snIndex - 1)
      val seedNodePort  = seedNode.substring(seedNode.lastIndexOf(":") + 1).toInt

      s"""akka.remote.netty.tcp.port=$seedNodePort\n"""
    } else ""

    val seedNodesLines = seedNodes.mkString("akka.cluster.seed-nodes = [\n\"", "\",\n\"", "\"\n]\n")

    val myConfig = ConfigFactory.parseString(nettyPortLine + seedNodesLines)

    myConfig.withFallback(config)
  }

  def apply(seedNodeIndex : Int, config: Config) = {

    require(seedNodeIndex > 0)

    val seedNodes     = seedNodesList(config)

    require(seedNodeIndex <= seedNodes.size)

    val seedNode      = seedNodes(seedNodeIndex - 1)

    new SeedNodeInfo(seedNodeIndex, seedNode)
  }

  def log(log: LoggingAdapter, config: Config) {

    val snIndex     = seedNodeIndex(config)
    val seedNodes   = seedNodesList(config)

    log.info(s"Seed Node Info: index=$snIndex, number of configured seed nodes is ${seedNodes.size}")

    for ((sn, index) <- seedNodes.zipWithIndex)
      log.info(s"${index + 1}: Seed Node $sn")

    if (snIndex > 0) {
      log.info(s"Cluster Node is running as a Seed Node $snIndex: ${seedNodes(snIndex - 1)}")
    } else {
      log.info("Cluster Node is NOT running as a Seed Node, see --help for more info")
    }
  }

  def consoleLog() {

    val config = ConfigFactory.load()

    for ((sn, index) <- seedNodesList(config).zipWithIndex)
      println(s"                      ${index + 1}: Seed Node $sn")

  }

}
