package com.gree.config

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkSessionSingleton {
  @transient private var instance: SparkSession = _

  def getConfig() = {
    new SparkConf().setAppName("InspectionStreaming")
      .set("spark.debug.maxToStringFields","5")
      .setMaster("local")
  }

  def getSc() = {
    instance.sparkContext
  }

  def getInstance(): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(getConfig())
        .getOrCreate()
    }
    instance
  }
}
