package com.modelfabric.akka.cluster

import akka.cluster.Member

/**
 * A Member Registry is an object that registers Akka Cluster Members
 */
trait MemberRegistry {

  def register(member: Member): Unit
  def deregister(member: Member): Unit
}
