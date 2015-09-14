package com.modelfabric.akka.actor

import akka.event.LoggingAdapter
import akka.actor.Actor
import akka.actor.Actor.Receive

//import spray.can.server.HttpServer

/**
 * Handle unknown messages by logging them
 */
trait CloseMessageHandler {

  val log : LoggingAdapter
  //val context : ActorContext

  //private def close(reason : spray.util.ClosedEventReason) {
  //  log.error(s"Close {}", reason)
  //  //context.stop(context.self)
  //}

  def closeMessage : Receive = {
    case akka.io.Tcp.Closed ⇒
  }
}

