package com.gree.utils

import java.sql.Timestamp

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JDouble, JInt, JString}

class NumberSerializer extends CustomSerializer[Long](_ => ( {
  case JString(x) => if (x.isEmpty) 0 else x.toLong
}, {
  case x: Int => JInt(x)
}
))

class DoubleSerializer extends CustomSerializer[Double](_ => ( {
  case JString(x) => if (x.isEmpty) 0 else x.toDouble
}, {
  case x: Double => JDouble(x)
}
))

class TimeSerializer extends CustomSerializer[Timestamp](_ => ( {
  case JInt(x) => new Timestamp(x.toLong)
}, {
  case x: Timestamp => JInt(x.getTime)
}
))


