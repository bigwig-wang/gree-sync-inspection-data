package com.gree

import java.util.regex.Pattern

import com.gree.DictionaryHandler._

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

  def get_base(inspectorCode: String): Base = {
    baseCodeBroadcastInstance.value.getOrElse(inspectorCode, Base("null", "null"))
  }

  //对于历史数据的处理
  def get_department(materialGroupCode: String, inspectorCode: String): Department = {
    val matchDepartment = materialGroupDepartmentBroadcastInstance
      .value.get(inspectorCode)
    if (matchDepartment.isEmpty) {
      //根据检验员来查
      inspectorDepartmentBroadcastInstance.value.getOrElse(inspectorCode, Department("null", "null"))
    } else {
      matchDepartment.getOrElse(Department("null", "null"))
    }
  }

  //对于新数据的处理
  def get_department_for_new_data(inspectorCode: String): Department = {
    //根据检验员来查
    inspectorDepartmentBroadcastInstance.value.getOrElse(inspectorCode, Department("null", "null"))
  }

  def get_is_commute(isCommute: String): Boolean = {
    "1".equals(isCommute)
  }
}
