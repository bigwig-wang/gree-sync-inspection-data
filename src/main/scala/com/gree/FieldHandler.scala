package com.gree

import java.util.regex.Pattern

import com.gree.DictionaryHandler._
import org.apache.spark.sql.Row

class FieldHandler extends Serializable {

  val ZERO: String = "0"
  val ONE: String = "1"

  def get_material_group_name_by_code(materialGroupCode: String): String = {
    val materialGroupMap = materialGroupBroadcastInstance.value
    if (materialGroupMap.contains(materialGroupCode)) {
      return materialGroupMap(materialGroupCode)
    }
    return "找不到对应物料组名"
  }

  def get_unqualified_reason_name_by_code(unqualifiedReasonCode: String): String = {
    val unqualifiedReasonMap = unqualifiedReasonBroadcastInstance.value
    if (unqualifiedReasonMap.contains(unqualifiedReasonCode)) {
      return unqualifiedReasonMap(unqualifiedReasonCode)
    }
    return "找不到对应不合格原因"
  }

  def get_inspection_conclusion(lastcertified: String): String = {
    if (ONE.equals(lastcertified)) {
      "不合格"
    }
    else if (ZERO.equals(lastcertified)) {
      "合格"
    }
    else {
      ""
    }
  }

  def get_inspector_code(username: String): String = {
    val regEx = "[^0-9]"
    val p = Pattern.compile(regEx)
    p.matcher(username).replaceAll("")
  }

  def get_inspector_name(username: String): String = {
    val reg = "[^\u4e00-\u9fa5]"
    username.replaceAll(reg, "")
  }

  def get_base(mappingCode: String): Row = {
    var row: Row = null
    val base = baseCodeBroadcastInstance.value.filter("mapping_code=" + mappingCode)
      .select("base_code", "base_name")
    if (base.count() > 0) {
      row = base.first()
    }
    row
  }

  def get_base_code(row: Row): String = {
    var baseCode = ""
    if(row != null) {
      baseCode = row.getString(0)
    }
    baseCode
  }

  def get_base_name(row: Row): String = {
    var baseName = ""
    if(row != null) {
      baseName = row.getString(1)
    }
    baseName
  }

  def get_department_code(row: Row): String = {
    var departmentCode = ""
    if(row != null) {
      departmentCode = row.getString(0)
    }
    departmentCode
  }

  def get_department_name(row: Row): String = {
    var departmentName = ""
    if(row != null) {
      departmentName = row.getString(1)
    }
    departmentName
  }

  //对于历史数据的处理
  def get_department(materialGroupCode: String, inspectorCode: String): Row = {
    var row: Row = null
    val matchDepartment = materialGroupDepartmentBroadcastInstance
      .value.filter("material_group_code=" + materialGroupCode)
      .select("department_code", "department_name")

    if (matchDepartment.count() == 0) {
      //根据检验员来查
      val matchInspectorDepartment = inspectorDepartmentBroadcastInstance
        .value.filter("inspector_code=" + inspectorCode)
        .select("department_code", "department_name")
      if (matchInspectorDepartment.count() > 0) {
        row = matchInspectorDepartment.first()
      }
    } else {
      row = matchDepartment.first()
    }
    row
  }

  //对于新数据的处理
  def get_department_for_new_data(inspectorCode: String): Row = {
    var row: Row = null
    //根据检验员来查
    val matchInspectorDepartment = inspectorDepartmentBroadcastInstance
      .value.filter("inspector_code=" + inspectorCode)
      .select("department_code", "department_name")
    if (matchInspectorDepartment.count() > 0) {
      row = matchInspectorDepartment.first()
    }
    row
  }

  def get_is_commute(isCommute: String): Boolean = {
    "1".equals(isCommute)
  }
}
