package com.modelfabric.akka.cluster

import akka.cluster.Cluster
import akka.event.Logging
import com.modelfabric.main.BooterProperties
import com.typesafe.config.Config

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Deals with setting up and/or (re)joining an Akka Cluster, running seed-nodes etc.
 */
class ClusterWrapper private (params: ClusterParams) {

  val name = BooterProperties.systemName

  lazy val (systemWrapper, cluster) = ClusterWrapperBuilder(params)

  lazy val log = Logging(systemWrapper.system, getClass)

  //
  // TODO: Support rejoins, by running AkkaClusterWrapperBuilder again
  //
  def waitUntilTerminated = {

    log.debug(s"Cluster akka://$name is awaiting termination")

    if (! params.config.getBoolean("launch.test")) Await.result(systemWrapper.system.whenTerminated, Duration.Inf)

    1 // We could support other exit codes here
  }
}

private[this] object ClusterWrapperBuilder {

  /**
   * The Seed Node Index is 1 based! 0 means that we're not running as a Seed Node.
   */
  def apply(params: ClusterParams): (SystemWrapper, Cluster) = {

    val name = BooterProperties.systemName

    val systemWrapper = new SystemWrapper(params.config)

    val log = Logging(systemWrapper.system, getClass)

    val cluster = {

      SeedNodeInfo.log(log, params.config)

      log.info(s"Creating Cluster Node $name")

      val kluster = Cluster(systemWrapper.system)

      require(kluster != null, "Could not create Cluster")

      log.info(s"Created Cluster ${kluster.selfAddress}")

      for(role <- kluster.selfRoles)
        log.info (s"Cluster has Role $role")

      /*
       * Deploy the cluster listener that will do some reporting on what happens with the Akka Cluster
       */
      ClusterListener(systemWrapper.system)

      kluster
    }

    (systemWrapper, cluster)
  }
}

case class ClusterParams(
  config: Config
)

object ClusterWrapper {

  def apply(params: ClusterParams) = new ClusterWrapper(params)
}
