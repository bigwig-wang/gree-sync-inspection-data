package com.gree

import java.security.PrivilegedExceptionAction
import java.util.Properties

import org.apache.hadoop.security.UserGroupInformation
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.SparkConf

object KuduContextSingleton {

  @transient private var instance: KuduContext = _

  def getInstance(): KuduContext = {

    if(instance == null) {
      val in = KuduContextSingleton.getClass.getClassLoader.getResourceAsStream("gree/kafka.properties")
      val properties = new Properties()
      properties.load(in)

      val kuduMaster: String = properties.getProperty("kudu.master")
      val spark = SparkSessionSingleton.getInstance(SparkSessionSingleton.getSparkConf())
      instance = UserGroupInformation.getLoginUser.doAs(new PrivilegedExceptionAction[KuduContext]() {
        @throws[Exception]
        override def run: KuduContext = new KuduContext(kuduMaster, spark.sparkContext)
      })
    }
    instance
  }

}
