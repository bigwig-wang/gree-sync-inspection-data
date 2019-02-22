package com.gree

import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast

object DictionaryHandler extends Serializable {
  var materialGroupBroadcastInstance: Broadcast[Map[String,String]] = _

  var unqualifiedReasonBroadcastInstance: Broadcast[Map[String,String]] = _

  //materialGroupCode => 对应多个科室  code => List<科室编码，科室名字>
  var materialGroupDepartmentBroadcastInstance: Broadcast[Map[String,String]] = _

  //inspectorCode => 对应一个科室   code => Object(科室编码， 科室名字)
  var InspectorDepartmentBroadcastInstance: Broadcast[Map[String,String]] = _

  def init_inspector_and_department: Unit = {
    //inspectorCode => (departmentCode, departmentName)
  }

  def init_material_group_and_department: Unit = {
    //materialGroupCode => List(departmentCode, departmentName)
  }

  def init_all_material_group: Unit = {
    val statement = StatementSingleton.getInstance()
    val sparkConf = new SparkConf().setMaster("local").setAppName("GreeKuduStreaming")
    val sparkSession = SparkSessionSingleton.getInstance(sparkConf)

    val rs = statement.executeQuery(new StringBuilder("select * from test.material_group").toString())
    var map: Map[String,String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    materialGroupBroadcastInstance = sparkSession.sparkContext.broadcast(map)
  }

  def init_all_unqualified_reason: Unit = {
    val statement = StatementSingleton.getInstance()
    val sparkConf = new SparkConf().setMaster("local").setAppName("GreeKuduStreaming")
    val sparkSession = SparkSessionSingleton.getInstance(sparkConf)

    val rs = statement.executeQuery(new StringBuilder("select * from test.material_group").toString())
    var map: Map[String,String] = Map()
    while (rs.next()) {
      map += (rs.getString(1) -> rs.getString(2))
    }
    unqualifiedReasonBroadcastInstance = sparkSession.sparkContext.broadcast(map)
  }

  def init_all_dictionary_data: Unit = {
    init_all_unqualified_reason
    init_all_material_group
  }
}
