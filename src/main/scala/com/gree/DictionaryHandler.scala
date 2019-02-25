package com.gree

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.DataFrame
import com.gree.utils.GreeConstants.{materialGroupDepartmentExcelPath, inspectorDepartmentExcelPath,
  baseExcelPath, materialGroupSqlStatement, unqualifiedReasonSqlStatement}

object DictionaryHandler extends Serializable {
  var materialGroupBroadcastInstance: Broadcast[Map[String,String]] = _

  var unqualifiedReasonBroadcastInstance: Broadcast[Map[String,String]] = _

  //materialGroupCode => 对应多个科室  code => Object<科室编码，科室名字>
  var materialGroupDepartmentBroadcastInstance: Broadcast[DataFrame] = _

  //inspectorCode => 对应一个科室   code => Object(科室编码， 科室名字)
  var inspectorDepartmentBroadcastInstance: Broadcast[DataFrame] = _

  var baseCodeBroadcastInstance: Broadcast[DataFrame] = _

  def init_base_code: Unit = {
    val service = SparkExcelUtil.apply
    val df = service.read(SparkSessionSingleton.getInstance(
      SparkSessionSingleton.getSparkConf()), materialGroupDepartmentExcelPath
      )
      .toDF()
    df.show()

    val sparkSession = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())
    inspectorDepartmentBroadcastInstance = sparkSession.sparkContext.broadcast(df)
  }

  def init_inspector_and_department: Unit = {
    //inspectorCode => (departmentCode, departmentName)
    val service = SparkExcelUtil.apply
    val df = service.read(SparkSessionSingleton.getInstance(
      SparkSessionSingleton.getSparkConf()), inspectorDepartmentExcelPath
      )
      .toDF("inspector_code", "inspector_name", "department_code", "department_name")
    df.show()

    val sparkSession = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())
    inspectorDepartmentBroadcastInstance = sparkSession.sparkContext.broadcast(df)
  }


  def init_material_group_and_department: Unit = {
    //materialGroupCode => (departmentCode, departmentName)
    val service = SparkExcelUtil.apply
    val df = service.read(SparkSessionSingleton.getInstance(
      SparkSessionSingleton.getSparkConf()), baseExcelPath
      )
      .toDF("material_group_code", "material_group_name", "department_code", "department_name")
    df.show()

    val sparkSession = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())
    materialGroupDepartmentBroadcastInstance = sparkSession.sparkContext.broadcast(df)
  }


  def init_all_material_group: Unit = {
    val statement = StatementSingleton.getInstance()
    val sparkSession = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())

    val rs = statement.executeQuery(materialGroupSqlStatement)
    var map: Map[String,String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    materialGroupBroadcastInstance = sparkSession.sparkContext.broadcast(map)
  }

  def init_all_unqualified_reason: Unit = {
    val statement = StatementSingleton.getInstance()
    val sparkSession = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())

    val rs = statement.executeQuery(unqualifiedReasonSqlStatement)
    var map: Map[String,String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    unqualifiedReasonBroadcastInstance = sparkSession.sparkContext.broadcast(map)
  }

  def init_all_dictionary_data: Unit = {
    init_all_unqualified_reason
    init_all_material_group
    init_material_group_and_department
    init_inspector_and_department
  }

  def main(args: Array[String]): Unit = {
    init_material_group_and_department
    init_inspector_and_department

    print(new FieldHandler().get_department("207", "2"))
  }
}
