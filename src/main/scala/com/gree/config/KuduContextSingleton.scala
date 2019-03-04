package com.gree.config

import java.security.PrivilegedExceptionAction

import org.apache.hadoop.security.UserGroupInformation
import org.apache.kudu.spark.kudu.KuduContext

object KuduContextSingleton {

  @transient private var instance: KuduContext = _

  def getInstance(config: KuduConfig): KuduContext = {
    if (instance == null) {
      instance = UserGroupInformation.getLoginUser.doAs(new PrivilegedExceptionAction[KuduContext]() {
        @throws[Exception]
        override def run: KuduContext = new KuduContext(config.master, SparkSessionSingleton.getSc())
      })
    }
    instance
  }

}
