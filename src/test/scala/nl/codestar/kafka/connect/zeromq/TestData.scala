package nl.codestar.kafka.connect.zeromq

import java.util

import scala.collection.JavaConverters._

object TestData {

  val port = "5563"
  val publisherUrl = s"tcp://*:$port"

  object Test1 {
    val url = s"tcp://localhost:$port"
    val allEnvelopes = Seq("envelope1", "envelope2")
    val subscribedEnvelopes: String = allEnvelopes.head
    val maxPollRecords = 10
    val kafkaTopic = "kafka-topic-1"

    lazy val settings: util.Map[String, String] = Map(
      ZeroMQSourceConnectorConfig.urlConf -> url,
      ZeroMQSourceConnectorConfig.envelopesConf -> subscribedEnvelopes,
      ZeroMQSourceConnectorConfig.maxPollRecordsConf -> maxPollRecords.toString,
      ZeroMQSourceConnectorConfig.kafkaTopicConf -> kafkaTopic,
    ).asJava

    lazy val config = new ZeroMQSourceConnectorConfig(settings)
  }

  object Test2 {
    val url = "tcp://pubsub.besteffort.ndovloket.nl:7664"
    val allEnvelopes = Seq("/RIG/NStreinpositiesInterface5")
    val envelopesToSubscribe: String = allEnvelopes.head
    val maxPollRecords = 10
    val kafkaTopic = "kafka-topic-1"

    lazy val settings: util.Map[String, String] = Map(
      ZeroMQSourceConnectorConfig.urlConf -> url,
      ZeroMQSourceConnectorConfig.envelopesConf -> envelopesToSubscribe,
      ZeroMQSourceConnectorConfig.maxPollRecordsConf -> maxPollRecords.toString,
      ZeroMQSourceConnectorConfig.kafkaTopicConf -> kafkaTopic,
    ).asJava

    lazy val config = new ZeroMQSourceConnectorConfig(settings)
  }

}