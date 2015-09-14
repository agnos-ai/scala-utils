package com.modelfabric.akka.actor

import akka.actor.Actor
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.Cluster

/**
 * A MemberEventSubscriber subscribes to the Akka Cluster Node Event messages
 */
trait MemberEventSubscriber extends Actor {

  val cluster = Cluster(context.system)

  /**
   * Subscribe to cluster changes, MemberUp.
   * Re-subscribe when restart
   */
  override def preStart() {
    cluster.subscribe(self, classOf[MemberUp])
    super.preStart()
  }

  override def postStop() {
    cluster.unsubscribe(self)
    super.postStop()
  }
}