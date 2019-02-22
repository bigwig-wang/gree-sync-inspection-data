package com.gree

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.json4s.{CustomSerializer, DefaultFormats}
import org.json4s.JsonAST.{JDouble, JInt, JString}
import org.json4s.jackson.JsonMethods.parse

object Main {
  class NumberSerializer extends CustomSerializer[Long](format => ( {
    case JString(x) => if (x.isEmpty) 0 else x.toLong
  }, {
    case x: Int => JInt(x)
  }
  ))

  class DoubleSerializer extends CustomSerializer[Double](format => ( {
    case JString(x) => if (x.isEmpty) 0 else x.toDouble
  }, {
    case x: Double => JDouble(x)
  }
  ))

  class TimeSerializer extends CustomSerializer[Timestamp](format => ( {
    case JInt(x) => new Timestamp(x.toLong)
  }, {
    case x: Timestamp => JInt(x.getTime)
  }
  ))

  def test_seri(): Unit = {
    val result = "{ \"result\": { \"totalCount\": 1292, \"items\": [{ \"categoryname\": \"235\", \"partcode\": \"03410023\", \"partname\": \"分流器\", \"enterprisename\": \"730110\", \"description\": \"郑州源泉制冷科技有限公司\", \"mlotno\": \"730110201808240026\","+
      "\"faillevel\": null, \"qcmodename\": \"抽检\", \"statename\": \"收货\", \"lastcertified\": \"合格\", \"username\": \"735016\", \"executeddate\": \"08/26/2018 09:51:50\", \"purchasercode\": \"734016\", \"remarks\": null, \"qcquantity\": \"5\", \"failquantity\":"+
      " \"0\", \"passquantity\": \"5\", \"failreason\": null, \"deliveryorderno\": \"730110180824016\", \"receivedquantity\": \"5694\", \"delivereddate\": \"08/24/2018 16:31:52\", \"finalcertified\": null, \"responsibleorganization\": null, \"sjremarks\": null, "+
      "\"failreasoncode\": null, \"failcode\": null }] } }"
    implicit val formats = new DefaultFormats {
      override def dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    } + new NumberSerializer() + new DoubleSerializer() + new TimeSerializer()
    val fi: List[ApiInspection] = (parse(result)\ "result" \ "items").extract[List[ApiInspection]]
    println(fi)
  }

  def main(args: Array[String]): Unit = {
    //初始化kerberos
    KerberosInitConfig.initKerberos()

    //初始化物料组
    DictionaryHandler.init_all_material_group

    KafkaConsumer.kafka_streaming()
  }



}
