package com.modelfabric.akka.cluster

import com.modelfabric.akka.actor._

/**
 * A ClusterSupervisor handles events for a node in the BKNOW cluster
 */
trait ClusterSupervisor
  extends UnknownMessageHandler
  with LocalMessageHandler
  with MemberEventSubscriber
  with MemberEventHandler
  with ClusterStateHandler {

  def receive = memberEventMessage orElse clusterStateMessage orElse localMessage orElse unknownMessage
}
