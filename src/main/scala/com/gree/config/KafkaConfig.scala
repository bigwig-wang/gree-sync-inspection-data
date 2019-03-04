package com.gree.config

import com.google.inject.Inject
import com.google.inject.name.Named
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaConfig {

  @Inject
  @Named("kafka.brokers")
  var brokerList: String = _

  @Inject
  @Named("kafka.security.protocol")
  var kafkaSecurityProtocol: String = _

  @Inject
  @Named("kafka.login.config.location")
  var kafkaLoginConfigLocation: String = _

  @Inject
  @Named("kerberos.krb5.location")
  var krb5Location: String = _

  @Inject
  @Named("kafka.group.id")
  var groupId: String = _

  @Inject
  @Named("kafka.sasl.kerberos.service.name")
  var kafkaServiceName: String = _

  @Inject
  @Named("kafka.topics")
  var topics: String = _

  def kafkaConfigs(): Map[String, Object] = {
    System.setProperty("java.security.krb5.conf", krb5Location)
    System.setProperty("java.security.auth.login.config", kafkaLoginConfigLocation)
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokerList
      , "key.deserializer" -> classOf[StringDeserializer]
      , "value.deserializer" -> classOf[StringDeserializer]
      , "group.id" -> groupId
      , "auto.offset.reset" -> "latest"
      , "enable.auto.commit" -> (true: java.lang.Boolean)
      , "security.protocol" -> "SASL_PLAINTEXT"
      , "sasl.kerberos.service.name" -> "kafka"
    )
    kafkaParams
  }

  def getTopics = {
    topics.split(",").toSet
  }

}
