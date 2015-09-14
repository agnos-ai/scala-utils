package com.modelfabric.akka.actor

import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.MemberStatus
import akka.actor.Actor.Receive
import com.modelfabric.akka.cluster.MemberRegistry

/**
 * An Akka Cluster State Handler can receive State messages
 */
trait ClusterStateHandler extends MemberRegistry {

  def handleCurrentState(state: CurrentClusterState): Unit =
    state.members.filter(_.status == MemberStatus.Up) foreach register

  def clusterStateMessage: Receive = {
    case state: CurrentClusterState => handleCurrentState(state)
  }
}
