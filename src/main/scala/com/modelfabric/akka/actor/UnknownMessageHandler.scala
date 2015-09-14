package com.modelfabric.akka.actor

import akka.actor.{ActorLogging, ActorRef, Actor}

/**
 * Handle unknown messages by logging them
 */
trait UnknownMessageHandler extends Actor with ActorLogging {

  private def logAnyRef(message_ : AnyRef, sender_ : ActorRef) {
    log.error("{} a received unknown message {} ({}) from {}", getClass.getName, message_, message_.getClass.getName, sender_)
  }

  private def logAny(message_ : Any, sender_ : ActorRef) {
    log.error("{} b received unknown message {} from {}", getClass.getName, message_, sender_)
  }

  def unknownMessage : Receive = {
    case message @ _ => message match {
      case m : AnyRef => logAnyRef(m, sender)
      case huh => logAny(huh, sender)
    }
  }
}
