package com.modelfabric.extension

import spray.http.Uri
import scala.language.implicitConversions

object UriExtensions {

  /**
   * Converts a java.net.URI to a spray.http.Uri
   */
  implicit def convertToSprayUri(uri : java.net.URI) : Uri = Uri.parseAbsolute(uri.toString)
}
