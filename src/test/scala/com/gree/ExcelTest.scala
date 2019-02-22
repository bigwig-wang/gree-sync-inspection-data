package com.gree

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType, _}
import org.scalatest._


class ExcelTest extends FlatSpec with BeforeAndAfter with GivenWhenThen {

  var spark: SparkSession = _

  val path = "/Users/wei.qi/workspace/tw_work/gree/gree-sync-inspection-data/data/科室物料组对应表.xlsx"

  val customSchema = StructType(Array(
    StructField("Keshi", StringType, nullable = false),
    StructField("Code", StringType, nullable = false)
  ))

  behavior of "excel like test"

  before {
    println("init..............")
    spark = SparkSession
      .builder
      .appName("ExcelTest").master("local")
      .getOrCreate()
  }

  it should "get data from file" in {
    val service = SparkExcelUtil.apply
    val df = service.read(spark, path)
    val count = df.count()
    assert(count > 0)
  }

  it should "get data from file by customSchema" in {
    val service = SparkExcelUtil.apply
    val df = service.read(spark, path, customSchema)
    val count = df.count()
    assert(count > 0)
  }

  it should "get data from file with sheet" in {
    val service = SparkExcelUtil.apply
    val df = service.read(spark, path, "'珠海总部科室编码'!")
    val count = df.count()
    assert(count > 0)
  }

  it should "get data from file with sheet and schema" in {
    val service = SparkExcelUtil.apply
    val df = service.read(spark, path, "'珠海总部科室编码'!A2",customSchema)
    val count = df.count()
    assert(count > 0)
  }

  it should "get data from broadcast " in {
    val service = SparkExcelUtil.apply
    val df = service.read(spark, path, "'珠海总部科室编码'!A2",customSchema)
    val count = df.count()

    When("count greater than 0")
    assert(count > 0)

    Then("broadcast")
    val broadcastVar = spark.sparkContext.broadcast(df)

    broadcastVar.value.show()
  }

  after {
    spark.stop()
    println("finish............")
  }
}
