package com.gree.utils

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkCsvUtil extends {
  def read(spark: SparkSession, path: String): DataFrame = {
    val df = spark.read.csv(path)
    df
  }

}


