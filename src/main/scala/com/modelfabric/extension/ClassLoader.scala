package com.modelfabric.extension

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem

/**
 * Impl class, @see ClassLoader
 */
class ClassLoaderImpl(system_ : ExtendedActorSystem) extends Extension {

  private val classLoader = system_.dynamicAccess.classLoader

  def load(className_ : String) = {
    classLoader.loadClass(className_)
  }
}

/**
 * Use ClassLoader to load an Akka Actor.
 *
 * Currently, all jars are loaded at startup time so it is not really necessary
 * to use the Akka class loader but eventually we could load service actors
 * dynamically from their own jars.
 */
object ClassLoader
    extends ExtensionId[ClassLoaderImpl]
    with ExtensionIdProvider {

  /*
   * The lookup method is required by ExtensionIdProvider, so we return 
   * ourselves here, this allows us to configure our extension to be loaded
   * when the ActorSystem starts up
   */
  override def lookup = ClassLoader

  /*
   * This method will be called by Akka to instantiate our Extension
   */
  override def createExtension(system_ : ExtendedActorSystem) = {
    new ClassLoaderImpl(system_)
  }
}
