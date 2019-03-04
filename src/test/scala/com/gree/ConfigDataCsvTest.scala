package com.gree

import com.gree.constant.DataConfig
import com.gree.utils.SparkCsvUtil
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType, _}
import org.scalatest._

class ConfigDataCsvTest extends FlatSpec with BeforeAndAfter with GivenWhenThen {

  var spark: SparkSession = _

  val path = "/Users/wei.qi/workspace/tw_work/gree/gree-sync-inspection-data/data/"

  val customSchema = StructType(Array(
    StructField("Keshi", StringType, nullable = false),
    StructField("Code", StringType, nullable = false)
  ))

  behavior of "excel like test"

  before {
    println("init..............")
    spark = SparkSession
      .builder
      .appName("ConfigDataTest").master("local")
      .getOrCreate()
  }

  it should "get base data from file" in {
    val df = SparkCsvUtil.read(spark, DataConfig.BASE_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(4) -> Base(r.getString(0), r.getString(1))))
      .reduce((a, b) => a ++ b)

    df.foreach(f => {
      println(f)
    })
    println(df.size)
    assert(df.size.equals(8))
  }

  it should "get material department data from file" in {
    val df = SparkCsvUtil.read(spark, DataConfig.MATERIAL_GROUP_DEPARTMENT_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(0) -> Department(r.getString(3), r.getString(2))))
      .reduce((a, b) => a ++ b)

    df.foreach(f => {
      println(f)
    })
    println(df.size)
    assert(df.contains("455"))
    assert(df.size.equals(95))
  }

  it should "get inspector department data from file" in {
    val df = SparkCsvUtil.read(spark, DataConfig.INSPECTOR_DEPARTMENT_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(3) -> Department(r.getString(1), r.getString(0))))
      .reduce((a, b) => a ++ b)
    df.foreach(f => {
      println(f)
    })
    println(df.size)
    assert(df.contains("491373"))
    assert(df.size.equals(40))

  }

  after {
    spark.stop()
    println("finish............")
  }
}
