package com.gree

import java.sql.Statement

import com.gree.constant.DataConfig
import com.gree.utils.SparkCsvUtil
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession

case class Department(departmentCode: String, departmentName: String)

case class Base(baseCode: String, baseName: String)

object DictionaryHandler extends Serializable {

  var materialGroupBroadcastInstance: Broadcast[Map[String, String]] = _

  var unqualifiedReasonBroadcastInstance: Broadcast[Map[String, String]] = _

  //materialGroupCode => 对应一个科室  code => Object<科室编码，科室名字>
  var materialGroupDepartmentBroadcastInstance: Broadcast[Map[String, Department]] = _

  //inspectorCode => 对应一个科室   code => Object(科室编码， 科室名字)
  var inspectorDepartmentBroadcastInstance: Broadcast[Map[String, Department]] = _

  var baseCodeBroadcastInstance: Broadcast[Map[String, Base]] = _

  def init_base_code(spark: SparkSession): Unit = {
    val df = SparkCsvUtil.read(spark, DataConfig.BASE_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(4) -> Base(r.getString(0), r.getString(1))))
      .reduce((a, b) => a ++ b)
    baseCodeBroadcastInstance = spark.sparkContext.broadcast(df)
  }

  def init_inspector_and_department(spark: SparkSession): Unit = {
    val df = SparkCsvUtil.read(spark, DataConfig.INSPECTOR_DEPARTMENT_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(3) -> Department(r.getString(1), r.getString(0))))
      .reduce((a, b) => a ++ b)
    inspectorDepartmentBroadcastInstance = spark.sparkContext.broadcast(df)
  }

  def init_material_group_and_department(spark: SparkSession): Unit = {
    val df = SparkCsvUtil.read(spark, DataConfig.MATERIAL_GROUP_DEPARTMENT_PATH)
      .rdd.filter(r => r.anyNull.equals(false)).map(r => Map(r.getString(0) -> Department(r.getString(3), r.getString(2))))
      .reduce((a, b) => a ++ b)
    materialGroupDepartmentBroadcastInstance = spark.sparkContext.broadcast(df)
  }

  def init_all_material_group(spark: SparkSession, statement: Statement): Unit = {
    val rs = statement.executeQuery(DataConfig.MATERIAL_GROUP_SQL_STATEMENT)
    var map: Map[String, String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    materialGroupBroadcastInstance = spark.sparkContext.broadcast(map)
  }

  def init_all_unqualified_reason(spark: SparkSession, statement: Statement): Unit = {
    val rs = statement.executeQuery(DataConfig.UNQUALIFIED_REASON_SQL_STATEMENT)
    var map: Map[String, String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    unqualifiedReasonBroadcastInstance = spark.sparkContext.broadcast(map)
  }

  def init(spark: SparkSession, statement: Statement): Unit = {
    init_base_code(spark)
    init_inspector_and_department(spark)
    init_material_group_and_department(spark)
    init_all_material_group(spark, statement)
    init_all_unqualified_reason(spark, statement)
  }

}
