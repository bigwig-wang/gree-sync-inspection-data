package com.gree

object Main {

  def main(args: Array[String]): Unit = {
    //初始化kerberos
    KerberosInitConfig.initKerberos()

    //初始化物料组
    DictionaryHandler.init_all_material_group

    KafkaConsumer.kafka_streaming()
  }



}
