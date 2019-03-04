package com.gree

import java.text.SimpleDateFormat
import java.util.Date

import com.google.inject.Guice
import com.gree.config._
import com.gree.module.BaseModule
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Minutes, StreamingContext}

class InspectionStreaming extends Logging {

  def handleData(): Unit = {
    val injector = Guice.createInjector(new BaseModule("gree"))
    val kafkaConfig = injector.getInstance(classOf[KafkaConfig])
    val kerberosConfig = injector.getInstance(classOf[KerberosConfig])
    val kuduConfig = injector.getInstance(classOf[KuduConfig])
    val jdbcConfig = injector.getInstance(classOf[JdbcConfig])
    val save_full_path = "/data/full/"
    val save_sample_path = "/data/sample/"

    kerberosConfig.init()
    val ssc = new StreamingContext(SparkSessionSingleton.getConfig(), Minutes(1))

    DictionaryHandler.init(SparkSessionSingleton.getInstance(), StatementSingleton.getInstance(jdbcConfig))

    val dStream = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](kafkaConfig.getTopics, kafkaConfig.kafkaConfigs()))
    dStream.map(_.value()).foreachRDD { rdd: RDD[String] =>
      if (rdd.count() > 0) {
        val spark = SparkSessionSingleton.getInstance()
        import spark.implicits._

        val apiData = rdd.flatMap(RecordTransform.parseRecord)

        val fullDF = apiData.filter(d => RecordTransform.get_full_data(d.inspection))
          .map(RecordTransform.assemblyFullData).toDF()
        logInfo("============full df  =========================================")
        fullDF.show()
        logInfo("============full df  =========================================")
        fullDF.write.mode(SaveMode.Append).parquet(save_full_path + getNowDate())
        val all_full_df = spark.read.parquet(save_full_path + getNowDate())
        all_full_df.dropDuplicates()
        //todo 按住键group 统计
        val full_df_result = RecordTransform.groupFullData(all_full_df)

        val sampleDF = apiData.filter(d => RecordTransform.get_sample_data(d.inspection))
          .map(RecordTransform.assemblySampleData).toDF()
        logInfo("============sample df  =========================================")
        sampleDF.show()
        logInfo("============sample df  =========================================")
        sampleDF.write.mode(SaveMode.Append).parquet(save_sample_path + getNowDate())
        val all_sample_df = spark.read.parquet(save_sample_path + getNowDate())
        all_sample_df.dropDuplicates()
        //todo 按住键group 统计
        val sample_df_result = RecordTransform.groupSampleData(all_sample_df)

        //kudu 插入数据
        val kuduContext = KuduContextSingleton.getInstance(kuduConfig)
        kuduContext.upsertRows(sample_df_result, "impala::gree.sample_inspection")
        kuduContext.upsertRows(full_df_result, "impala::gree.full_inspection")
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }

  def getNowDate(): String = {
    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    dateFormat.format(now)
  }
}

object InspectionStreaming {
  def main(args: Array[String]): Unit = {
    new InspectionStreaming().handleData()
  }
}
