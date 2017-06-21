package com.modelfabric.akka.actor

import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp, MemberEvent}
import akka.actor.Actor.Receive
import com.modelfabric.akka.cluster.MemberRegistry

/**
 * A Member-Event Handler can receive Akka Cluster Member events
 */
trait MemberEventHandler extends MemberRegistry {


  def memberUp(event: MemberEvent): Unit = register(event.member)
  def memberRemoved(event: MemberEvent): Unit = deregister(event.member)

  def memberEventMessage : Receive = {
    /*
     * A new member has joined the cluster and its status has been changed to Up.
     */
    case event : MemberUp ⇒ memberUp(event)
    /*
     * Member completely removed from the cluster.
     */
    case event : MemberRemoved => memberRemoved(event)
  }
}
