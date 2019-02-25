package com.gree.utils

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.mutable.ListBuffer

object TimeUtils {
  def getBetweenDates(start: String, end: String) = {
    val startData = new SimpleDateFormat("yyyy-MM-dd").parse(start); //定义起始日期
    val endData = new SimpleDateFormat("yyyy-MM-dd").parse(end); //定义结束日期

    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    var buffer = new ListBuffer[String]
    buffer += dateFormat.format(startData.getTime())
    val tempStart = Calendar.getInstance()

    tempStart.setTime(startData)
    tempStart.add(Calendar.DAY_OF_YEAR, 1)

    val tempEnd = Calendar.getInstance()
    tempEnd.setTime(endData)
    while (tempStart.before(tempEnd)) {
      buffer += dateFormat.format(tempStart.getTime())
      tempStart.add(Calendar.DAY_OF_YEAR, 1)
    }
    buffer += dateFormat.format(endData.getTime())
    buffer.toList
  }

}
