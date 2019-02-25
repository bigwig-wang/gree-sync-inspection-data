package com.gree.utils

import com.crealytics.spark.excel._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}


class SparkExcelUtils {
  def read(spark: SparkSession, path: String, sheet: String, customSchema: StructType): DataFrame = {

    val df = spark.read.excel(
      useHeader = false,
      dataAddress = sheet,
      timestampFormat = "MM-dd-yyyy HH:mm:ss" // Optional, default: yyyy-mm-dd hh:mm:ss[.fffffffff]
    ).schema(customSchema).load(path)
    df
  }

  def read(spark: SparkSession, path: String, sheet: String): DataFrame = {
    val df = spark.read.excel(
      useHeader = true,
      dataAddress = sheet, // Optional, default: "A1"
      timestampFormat = "MM-dd-yyyy HH:mm:ss" // Optional, default: yyyy-mm-dd hh:mm:ss[.fffffffff]
    ).load(path)
    df
  }

  def read(spark: SparkSession, path: String, customSchema: StructType): DataFrame = {
    this.read(spark, path, "A2", customSchema)
  }

  def read(spark: SparkSession, path: String): DataFrame = {
    this.read(spark, path, "A1")
  }
}

object SparkExcelUtils {

  def apply: SparkExcelUtils = new SparkExcelUtils()

}
