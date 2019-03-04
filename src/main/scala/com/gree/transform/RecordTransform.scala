package com.gree

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import com.gree.utils.{DoubleSerializer, NumberSerializer, TimeSerializer}
import org.apache.spark.sql.DataFrame
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

object RecordTransform {

  val FILTER_STATE_NAME: String = "带全检"
  val SAMPLE_TYPE: String = "抽检"
  val FULL_TYPE: String = "全检"

  def parseRecord(record: String): List[InspectionBase] = {
    implicit val formats = new DefaultFormats {
      override def dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    } + new NumberSerializer() + new DoubleSerializer() + new TimeSerializer()
    val fi: List[ApiInspection] = (parse(record) \ "items").extract[List[ApiInspection]]
    val computer: String = (parse(record) \ "computer").extract[String]
    val baseList = fi.map(f => InspectionBase(computer, f))
    baseList
  }

  def assemblySampleData(ib: InspectionBase): SampleInspection = {
    val handler = new FieldHandler()
    val f = ib.inspection
    val department = handler.get_department(f.categoryname, f.username)
    val base = handler.get_base(ib.basecode)

    SampleInspection(f.enterprisename, f.description, f.partname, f.partcode,
      f.categoryname, handler.get_material_group_name_by_code(f.categoryname),
      f.mlotno, f.faillevel, f.qcmodename, f.statename, handler.get_inspection_conclusion(f.lastcertified),
      handler.get_inspector_code(f.username), handler.get_inspector_name(f.username), f.executeddate,
      f.qcquantity, f.failquantity, f.passquantity,
      base.baseCode, base.baseName,
      department.departmentCode, department.departmentName,
      handler.get_is_commute(f.finalcertified), f.receivedquantity, f.delivereddate, f.sjremarks, f.deliveryorderno,
      Option(f.failcode).getOrElse("null"), handler.get_unqualified_reason_name_by_code(f.failcode), f.responsibleorganization, f.purchasercode, f.remarks,
      new Timestamp(new Date().getTime), new Timestamp(new Date().getTime))
  }

  def assemblyFullData(ib: InspectionBase): FullInspection = {
    val handler = new FieldHandler()
    val f = ib.inspection
    val department = handler.get_department(f.categoryname, f.username)
    val base = handler.get_base(ib.basecode)
    FullInspection(f.enterprisename, f.description, f.partname, f.partcode,
      f.categoryname, handler.get_material_group_name_by_code(f.categoryname),
      f.mlotno, f.faillevel, f.executeddate,
      base.baseCode, base.baseName,
      department.departmentCode, department.departmentName,
      f.qcquantity, f.failquantity,
      f.passquantity, f.passquantity / f.qcquantity,
      Option(f.failreasoncode).getOrElse("null"), handler.get_unqualified_reason_name_by_code(f.failreasoncode), 6.0,
      f.failquantity,
      new Timestamp(new Date().getTime), new Timestamp(new Date().getTime))
  }

  def groupFullData(df: DataFrame): DataFrame = {

    df
  }

  def groupSampleData(df: DataFrame): DataFrame = {

    df
  }

  def get_full_data(s: ApiInspection) = {
    s.qcmodename.contains(FULL_TYPE) && !s.statename.contains(FILTER_STATE_NAME)
  }

  def get_sample_data(s: ApiInspection) = {
    s.qcmodename.contains(SAMPLE_TYPE)
  }
}
