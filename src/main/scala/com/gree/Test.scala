package com.gree

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.gree.KafkaConsumer.{DoubleSerializer, NumberSerializer, TimeSerializer}
import org.json4s.{CustomSerializer, DefaultFormats}
import org.json4s.JsonAST.JInt
import org.json4s.jackson.JsonMethods.parse

object Test {
  class TimeSerializer extends CustomSerializer[Timestamp](format => ( {
    case JInt(x) => new Timestamp(x.toLong)
  }, {
    case x: Timestamp => JInt(x.getTime)
  }
  ))

  case class TestCase(categoryname:String,partcode:String,failcode:String)

}
