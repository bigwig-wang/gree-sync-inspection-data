package com.gree

import java.util.regex.Pattern

import com.gree.DictionaryHandler.materialGroupBroadcastInstance
import org.apache.commons.lang3.StringUtils

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

  def get_base_code(code: String): String = {
    "基地code"
  }

  def get_base_name(code: String): String = {
    "基地名字"
  }

  def get_department_code(code: String): String = {
    "部门code"
  }

  def get_department_name(code: String): String = {
    "部门名字"
  }

  def get_total_unqualified_rate(): Double = {
    0.00
  }

  def get_unqualified_reason_code(code: String): String = {
    if (StringUtils.isBlank(code)) return "code"
    code
  }

  def get_unqualified_reason_name(code: String): String = {
    "不合格原因"
  }

  def get_unqualified_rate_for_reason(): Double = {
    100.0
  }


  def get_is_commute(isCommute: String): Boolean = {
    "1".equals(isCommute)
  }
}
