package com.modelfabric.akka.actor

import akka.actor.{ActorLogging, Terminated, Actor, ActorRef}
import scala.collection.IndexedSeq
import com.modelfabric.akka.message.BackendRegistration

/**
 * A BackendMessageHandler can receive messages from a Backend-node to register the Backend-node to the Frontend-node.
 * It maintains a collection of registered backends.
 */
trait BackendMessageHandler extends Actor with ActorLogging {

  private var backends = IndexedSeq.empty[ActorRef]

  protected def hasNoBackends = backends.isEmpty

  protected def randomBackend(index: Int): ActorRef = backends(index % backends.size)

  def backendMessage: Receive = {
    case BackendRegistration if ! backends.contains(sender) ⇒
      log.info("Received BackendRegistration, adding sender to list of backends")
      context watch sender // triggers Terminated message when the sender-actor dies
      backends = backends :+ sender

    case _ @ BackendRegistration =>
      log.info("Received BackendRegistration")

    case Terminated(a) ⇒
      log.info("Received Terminated, removing sender from list of backends")
      backends = backends.filterNot(_ == a)
  }
}
