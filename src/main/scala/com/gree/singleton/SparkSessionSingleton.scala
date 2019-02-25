package com.gree.singleton

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkSessionSingleton {
  @transient private var instance: SparkSession = _
  @transient private var sparkConf: SparkConf = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()
    }
    instance
  }

  def getSparkConf(): SparkConf = {
    if (sparkConf == null) {
      sparkConf = new SparkConf().setAppName("GreeCleanData").setMaster("local")
    }
    sparkConf
  }
}
