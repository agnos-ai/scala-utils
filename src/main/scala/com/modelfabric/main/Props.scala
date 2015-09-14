package com.modelfabric.main

import scala.io.Source
import java.io.InputStream

import scala.collection.JavaConverters._
import scala.io.Source

class Props(private val inputStream : java.io.InputStream) {

  lazy private val source = {

    val s = Source.fromInputStream(inputStream)

    require(s != null)

    for (line <- s.getLines())
      println(line)

    s
  }

  lazy val properties = {

    val is = inputStream

    require(is != null)

    val props = new java.util.Properties()
    try {
      props.load(is)
    } catch {
      case ex : Throwable => {
        println("Some exception: " + ex.getMessage + " class=" + ex.getClass.getName)
      }
    }
    val map = props.asScala

    /*
    println(s"Read the following keys and values from $fileName:")
    for((key, value) <- map) println(key + "\t= " + value)
    println("----")
    */

    map
  }
}

object Props {

  private def inputStream(fileName : String) = getClass.getClassLoader.getResourceAsStream(fileName)

  def apply(fileName : String) = {

    var is = inputStream(fileName)

    if (is == null)
      is = inputStream("./target/scala-2.10/resource_managed/main/booter.properties")

    require(is != null, fileName + " needs to be found on the classpath")
    require(is.available() > 0)

    new Props(is)
  }
}
