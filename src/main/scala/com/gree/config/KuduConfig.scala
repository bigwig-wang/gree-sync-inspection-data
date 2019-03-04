package com.gree.config

import com.google.inject.Inject
import com.google.inject.name.Named
import org.apache.kafka.common.serialization.StringDeserializer

class KuduConfig {

  @Inject
  @Named("kudu.master")
  var master: String = _

}
