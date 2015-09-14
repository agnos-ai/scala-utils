package com.modelfabric.akka.cluster

import akka.actor.ActorSystem
import akka.event.Logging
import com.modelfabric.main.BooterProperties
import com.typesafe.config.Config
import scala.util.{Failure, Try, Success}

/**
 * AkkaSystemWrapper is a wrapper around the ActorSystem instance adding a few bells and whistles around the ActorSystem
 * instance. The main purpose of this class is to make it easier to re-create the whole thing when the Node has
 * to rejoin the cluster (which is dealt with by AkkaClusterWrapper)
 */
class SystemWrapper(akkaConfig: Config) {

  //
  // Will we joing the Akka Cluster?
  //
  val willJoinCluster = akkaConfig.getBoolean("launch.join-cluster")
  //
  // When we're going to join the Akka Cluster then we can not use our own unique system name but have to use the
  // cluster name for the ActorSystem as well.
  //
  val name = if (willJoinCluster) BooterProperties.clusterName else BooterProperties.systemName
  val path = s"akka://$name"

  /**
   * Get or create an Akka System
   */
  lazy val system : ActorSystem = {

    val zystem = Try(ActorSystem(name, akkaConfig)) match {
      case Success(s) => s
      case Failure(e) =>
        sys.error(s"ERROR: ${e.getMessage} while creating Actor System $path")
    }

    val l = Logging(zystem, getClass)

    l.debug(s"Akka System $path has just been created")

    zystem registerOnTermination {
      //
      // This gets called after shutdown of the ActorSystem, so do not refer to it or to the standard log
      //
      //println(s"System $path has been terminated")
      //
      // Actually, the isTerminated call would still return false here. Not sure why but this is about the last
      // line of code to be executed anyway so leave it. If we're going to do all sorts of cleanup here then
      // be aware of that though.
      //
    }

    sys addShutdownHook {
      l.info("Received Ctrl+C, shutting down")
      zystem.shutdown()
    }

    zystem
  }

  lazy val log = Logging(system, getClass)

  def waitUntilTerminated = {

    log.debug(s"System $path is awaiting termination")

    if (! akkaConfig.getBoolean("launch.test")) system.awaitTermination()

    1 // We could support other exit codes here
  }
}
