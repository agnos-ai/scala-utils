package com.modelfabric.akka.actor

import akka.actor.Actor.Receive

/**
 * A Local Message Handler implements the localReceive method that is called for any non-standard messages that
 * are usually implemented by the Actor classes at the bottom of the class tree.
 */
trait LocalMessageHandler {

  def localMessage : Receive

}
