package com.gree.utils

import java.util.Properties

object PropertyUtil {
  def loadFile(prefix: String): Properties = {
    val in = this.getClass.getClassLoader.getResourceAsStream(prefix)
    val properties = new Properties()
    properties.load(in)
    properties
  }
}
