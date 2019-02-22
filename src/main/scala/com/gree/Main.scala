package com.gree

object Main {

  def main(args: Array[String]): Unit = {
    //初始化kerberos
    KerberosInitConfig.initKerberos()

    //初始化物料组
    DictionaryHandler.init_all_material_group

    //删除数据
    StatementSingleton.getInstance().execute("delete from gree.sample_inspection")
    StatementSingleton.getInstance().execute("delete from gree.full_inspection")
    KafkaConsumer.kafka_streaming()
  }

}
