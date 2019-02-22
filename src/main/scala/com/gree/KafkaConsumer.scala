package com.gree

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.apache.commons.lang3.StringUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.JsonAST.{JDouble, JInt, JString}
import org.json4s.jackson.JsonMethods._
import org.json4s.{CustomSerializer, DefaultFormats, _}

object KafkaConsumer {
  val FILTER_STATE_NAME: String = "带全检"
  val SAMPLE_TYPE: String = "抽检"
  val FULL_TYPE: String = "全检"

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

  def streaming(): Unit = {
    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.foldLeft(0)(_ + _)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }
    //创建StreamingContext
    val conf = new SparkConf().setMaster("local[*]").setAppName("StreamTest")
    val sc = new StreamingContext(conf, Seconds(5))
    sc.checkpoint("/Users/dhwang/gree/cdh/kk/")

    val lines = sc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val wordDstream = words.map(x => (x, 1))
    val stateDstream = wordDstream.updateStateByKey[Int](updateFunc)
    stateDstream.print()
    sc.start()
    sc.awaitTermination()
  }


  def kafka_streaming(): Unit = {
    System.setProperty("java.security.krb5.conf", "/Users/dhwang/gree/cdh/keytab/krb5.conf")
    System.setProperty("java.security.auth.login.config", "/Users/dhwang/gree/cdh/keytab/jaas-cache.conf")
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
    val brokers = "dhwang1:9092"
    val topics = "test"

    println("kafka.brokers:" + brokers)
    println("kafka.topics:" + topics)
    if (StringUtils.isEmpty(brokers) || StringUtils.isEmpty(topics)) {
      println("未配置Kafka信息")
      System.exit(0)
    }
    val topicsSet = topics.split(",").toSet
    val spark = SparkSession.builder().appName("GreeKuduStreaming").master("local[2]").getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(30)) //设置Spark时间窗口，每5s处理一次
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers
      , "key.deserializer" -> classOf[StringDeserializer]
      , "value.deserializer" -> classOf[StringDeserializer]
      , "group.id" -> "1"
      , "auto.offset.reset" -> "latest"
      , "enable.auto.commit" -> (true: java.lang.Boolean)
      , "security.protocol" -> "SASL_PLAINTEXT"
      , "sasl.kerberos.service.name" -> "kafka"
    )

    val dStream = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    dStream.map(_.value()).foreachRDD { rdd: RDD[String] =>
      if(rdd.count() > 0){
        val spark = SparkSessionSingleton.getInstance(ssc.sparkContext.getConf)
        val apiData = rdd.map(getRecord).first()

        import spark.implicits._
        val fullDF = apiData.filter(s => get_full_data(s)).map(assemblyFullData).toDF()
        fullDF.show()
        val sampleDF = apiData.filter(get_sample_data).map(assemblySampleData).toDF()
        sampleDF.show()

        //kudu 插入数据
        val kuduContext = KuduContextSingleton.getInstance()
        kuduContext.insertRows(sampleDF, "impala::test.sample_inspection")
        kuduContext.insertRows(fullDF, "impala::test.full_inspection")
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }

  private def assemblySampleData(f: ApiInspection): SampleInspection = {
    val handler = new FieldHandler()
    SampleInspection(f.enterprisename, f.description, f.partname, f.partcode,
      f.categoryname, handler.get_material_group_name_by_code(f.categoryname),
      f.mlotno, f.faillevel, f.qcmodename, f.statename, handler.get_inspection_conclusion(f.lastcertified),
      handler.get_inspector_code(f.username), handler.get_inspector_name(f.username), f.executeddate,
      f.qcquantity, f.failquantity, f.passquantity,
      handler.get_base_code("1"), handler.get_base_name("1"), handler.get_department_code("1"), handler.get_department_name("1"),
      handler.get_is_commute(f.finalcertified), f.receivedquantity, f.delivereddate, f.sjremarks, f.deliveryorderno,
      f.failcode, "抽检不合格原因", f.responsibleorganization, f.purchasercode, f.remarks)
  }


  private def assemblyFullData(f: ApiInspection): FullInspection = {
    val handler = new FieldHandler()
    FullInspection(f.enterprisename, f.description, f.partname, f.partcode,
      f.categoryname, handler.get_material_group_name_by_code(f.categoryname),
      f.mlotno, f.faillevel, f.executeddate,
      handler.get_base_code("1"), handler.get_base_name("1"), handler.get_department_code("1"), handler.get_department_name("1"),
      f.qcquantity, f.failquantity,
      f.passquantity, handler.get_total_unqualified_rate(),
      handler.get_unqualified_reason_code(f.failreasoncode), handler.get_unqualified_reason_name(f.failreasoncode),
      6.0, handler.get_unqualified_rate_for_reason)
  }

  private def get_full_data(s: ApiInspection) = {
    s.qcmodename.contains(FULL_TYPE) && !s.statename.contains(FILTER_STATE_NAME)
  }

  private def get_sample_data(s: ApiInspection) = {
    s.qcmodename.contains(SAMPLE_TYPE)
  }


  def getRecord(record: String): List[ApiInspection] = {
    println("===================="+record+"======")
    implicit val formats = new DefaultFormats {
      override def dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    } + new NumberSerializer() + new DoubleSerializer() + new TimeSerializer()
    val fi: List[ApiInspection] = (parse(record) \ "result" \ "items").extract[List[ApiInspection]]
    fi
  }

}
