package com.gree.schdule

import java.util.Properties

import com.gree.kafka.KafkaProducer
import com.gree.kafka.KafkaConsumer
import com.gree.kafka.KafkaProducer.KafkaProducerConfigs
import com.gree.utils.HttpClientUtils.get_with_params
import com.gree.utils.TimeUtils.getBetweenDates

object SyncGreeApiData {
  def main(args: Array[String]): Unit = {

    //定时任务开始 区分历史数据 和最新数据，历史数据一次跑完
    //新产生的数据每15分钟取一次
    //历史数据
    sync_gree_history_data

    //TODO::新数据 通过定时任务 新数据和历史数据处理逻辑不一致，应该如何做比较好
  }

  def get_api_data_by_params(url: String, computer: Integer, startDateTime: String, endDateTime: String
                             , skipCount: Integer, maxResultCount: Integer): Unit = {
    //当前只取珠海基地的数据，code为4
    val data = get_with_params(url, computer, startDateTime, endDateTime, skipCount, maxResultCount)
    if (KafkaConsumer.getRecord(data).nonEmpty) {
      //TODO::topic名字后面需要改成从配置文件读取
      KafkaProducer.produce("test", data)

      //TODO::偏移量存在redis里面，防止出现变量共享的问题，需要集成redis
      val offset = skipCount + 1000
      get_api_data_by_params(url, computer, startDateTime, endDateTime, offset, maxResultCount)
    }
  }

  def sync_gree_new_data(): Unit = {
    val in = KafkaProducerConfigs.getClass.getClassLoader.getResourceAsStream("gree/api.properties")
    val properties = new Properties()
    properties.load(in)


  }

  def sync_gree_history_data(): Unit = {
    val in = KafkaProducerConfigs.getClass.getClassLoader.getResourceAsStream("gree/api.properties")
    val properties = new Properties()
    properties.load(in)

    //TODO:: 当前只取了珠海基地数据，后面需要接入所有基地
    var url: String = properties.getProperty("gree.api.url")
    var computer: Integer = 4
    var startDateTime: String = properties.getProperty("gree.api.startTime")
    var endDateTime: String = properties.getProperty("gree.api.endTime")
    var skipCount: Integer = 0
    var maxResultCount: Integer = 1000

    val timeList = getBetweenDates(startDateTime, endDateTime)
    for (i <- 0 until timeList.size - 2) {
      val startTime = timeList.apply(i)
      val endTime = timeList.apply(i+1)
      //按照天取数据
      get_api_data_by_params(url, computer, startTime, endTime, skipCount, maxResultCount)
    }
  }
}
