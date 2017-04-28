package com.modelfabric.akka.cluster

import akka.actor.{ActorRef, ActorSystem, Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

/**
 * AkkaClusterListener is a simple actor that logs the important events that happen on the Akka Cluster
 */
@deprecated("Stop functional after migrating to akka 2.5.0","2017-04-28")
class ClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  def superVisor = context.system.actorSelection("/user/supervisor")

  override def preStart(): Unit = cluster.subscribe(self, classOf[ClusterDomainEvent])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case state: CurrentClusterState ⇒
      log.info("Current members: {}", state.members.mkString(", "))

    /*
     * A new member has joined the cluster and its status has been changed to Up.
     */
//  case msg @ MemberUp(member) ⇒ superVisor forward msg // this only works in Akka 2.3.x
    case msg @ MemberUp(member) =>
      log.info(s"Forwarding MemberUp($member) message to supervisor")
      superVisor.tell(msg, sender)

    /*
     * A member is leaving the cluster and its status has been changed to Exiting Note that the node might already have
     * been shutdown when this event is published on another node.
     */
    case MemberExited(member) =>
      log.info(s"Member is Exiting: ${member.address}")

    /*
     * Member completely removed from the cluster.
     */
    case MemberRemoved(member, previousStatus) ⇒
      log.info("Member is Removed: {} after {}", member.address, previousStatus)

    /*
     * A member is considered as unreachable, detected by the failure detector of at least one other node.
     */
    case UnreachableMember(member) ⇒
      log.info("Member detected as unreachable: {}", member)

//    case _ : ClusterMetricsChanged => // ignore

    case m1 : ClusterDomainEvent ⇒ log.info("ClusterDomainEvent: " + m1) // ignore

    case m2 @ _ => log.info("Unknown message: " + m2)
  }
}

object ClusterListener {

  val actorName = "cluster-listener"

  def apply(system: ActorSystem): ActorRef = system.actorOf(akka.actor.Props[ClusterListener], name = actorName)

}