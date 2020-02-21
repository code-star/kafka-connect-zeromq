package nl.codestar.kafka.connect.zeromq

import java.util

object TestData {

  val port = "5563"
  val publisherUrl = s"tcp://*:$port"

  object Test1 {
    val url = s"tcp://localhost:$port"
    val allEnvelopes = Seq("envelope1", "envelope2")
    val subscribedEnvelopes: String = allEnvelopes.head
    val maxPollRecords = 10
    val kafkaTopic = "kafka-topic-1"

    val settings = new util.HashMap[String, String]()
    settings.put(ZeroMQSourceConnectorConfig.urlConf, url)
    settings.put(ZeroMQSourceConnectorConfig.envelopesConf, subscribedEnvelopes)
    settings.put(ZeroMQSourceConnectorConfig.maxPollRecordsConf, maxPollRecords.toString)
    settings.put(ZeroMQSourceConnectorConfig.kafkaTopicConf, kafkaTopic)

    val config = new ZeroMQSourceConnectorConfig(settings)
  }

  object Test2 {
    val url = "http://gtfs.ovapi.nl/nl/vehiclePositions.pb"
    val port = "7664"
    val allEnvelopes = Seq("/RIG/NStreinpositiesInterface5")
    val envelopesToSubscribe: String = allEnvelopes.head
    val kafkaTopic = "vehicle-positions"

    val settings = new util.HashMap[String, String]()
    settings.put(ZeroMQSourceConnectorConfig.urlConf, url)
    settings.put(ZeroMQSourceConnectorConfig.envelopesConf, envelopesToSubscribe)
    settings.put(ZeroMQSourceConnectorConfig.kafkaTopicConf, kafkaTopic)

    val config = new ZeroMQSourceConnectorConfig(settings)
  }

}