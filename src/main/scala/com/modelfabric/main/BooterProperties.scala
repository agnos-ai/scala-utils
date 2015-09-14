package com.modelfabric.main

/**
 * Loads booter.properties which contains some generated Maven values
 *
 * booter.cluster.name   = unknown
 * booter.system.name    = unknown
 * booter.system.version = 0.1
 * booter.scala.version  = 0
 * booter.git.hash.long  = 0
 * booter.git.hash.short = 0
 * booter.git.branch     = unknown
 * booter.generated.at   = 0
 */
object BooterProperties {

  val properties = Props("booter.properties").properties

  val systemName = properties.getOrElse("booter.system.name", "unknown")
  val clusterName = properties.getOrElse("booter.cluster.name", systemName)
  val systemVersion = properties.getOrElse("booter.system.version", "0.1")
  val systemRelease = properties.getOrElse("booter.git.hash.short", "0")
  val systemHash = properties.getOrElse("booter.git.hash.long", "0")
  val systemBranch = properties.getOrElse("booter.git.branch", "unknown")
  val scalaVersion = properties.getOrElse("booter.scala.version", "0")
  val generatedAt = properties.getOrElse("booter.generated.at", "0")

  def systemVersionFull = s"$systemVersion-$systemRelease"

  def system = s"$systemName $systemVersionFull"
}
