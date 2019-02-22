package com.gree

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

object KafkaProducer {

  case class KafkaProducerConfigs() {
    val in = KafkaProducerConfigs.getClass.getClassLoader.getResourceAsStream("gree/kafka.properties")
    val properties = new Properties()
    properties.load(in)

    private val krb5Location: String = properties.getProperty("kerberos.krb5.location")
    private val kafkaLoginConfigLocation: String = properties.getProperty("kafka.login.config.location")
    private val brokerList: String = properties.getProperty("kafka.brokers")
    private val kuduMaster: String = properties.getProperty("kudu.master")
    private val kafkaTopics: String = properties.getProperty("kafka.topics")
    private val kafkaServiceName: String = properties.getProperty("kafka.sasl.kerberos.service.name")
    private val kafkaSecurityProtocol: String = properties.getProperty("kafka.security.protocol")

    System.setProperty("java.security.krb5.conf", krb5Location)
    System.setProperty("java.security.auth.login.config", kafkaLoginConfigLocation)
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaSecurityProtocol)
    props.put("sasl.kerberos.service.name", kafkaServiceName)
  }

  val producer = new KafkaProducer[String, String](KafkaProducerConfigs().props)

  def produce(topic: String, messages: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic, messages))
    producer.close(100L, TimeUnit.MILLISECONDS)
  }

  def main(args: Array[String]): Unit = {
    val result = HttpClientUtils.get("http://localhost:8080/hello")
    produce("test", result)
  }
}
