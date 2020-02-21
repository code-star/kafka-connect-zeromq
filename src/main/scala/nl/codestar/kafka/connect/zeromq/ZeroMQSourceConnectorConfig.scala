package nl.codestar.kafka.connect.zeromq

import java.util

import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

class ZeroMQSourceConnectorConfig(settings: util.Map[String, String])
  extends AbstractConfig(ZeroMQSourceConnectorConfig.definition, settings) {

  def url: String = getString(ZeroMQSourceConnectorConfig.urlConf)
  def maxPollRecords: Integer = getInt(ZeroMQSourceConnectorConfig.maxPollRecordsConf)

}

object ZeroMQSourceConnectorConfig {
  val urlConf = "zeromq.url"
  val envelopesConf = "zeromq.envelopes"
  val pollIntervalConf = "zeromq.pollInterval"
  val maxBackoffConf = "zeromq.maxBackoff"
  val maxPollRecordsConf = "zeromq.maxPollRecords"
  val bufferSizeConf = "zeromq.bufferSize"
  val nrIoThreadsConf = "zeromq.nrIoThreads"
  val kafkaTopicConf = "zeromq.kafka.topic"

  val definition: ConfigDef = new ConfigDef()
    .define(urlConf, Type.STRING, Importance.HIGH, "zeromq url")
    .define(envelopesConf, Type.LIST, Importance.HIGH, "zeromq list of envelopes")
    .define(pollIntervalConf, Type.STRING, "PT0.1S", Importance.MEDIUM, "how often the zeromq source is polled; ISO8601 duration")
    .define(maxBackoffConf, Type.STRING,"PT5S", Importance.MEDIUM, "on failure, exponentially backoff to at most this ISO8601 duration")
    .define(maxPollRecordsConf, Type.INT, 1000, Importance.MEDIUM, "maximum number of records returned per poll")
    .define(bufferSizeConf, Type.INT, 5000, Importance.MEDIUM, "zeromq bufferSize")
    .define(nrIoThreadsConf, Type.INT, 1, Importance.LOW, "zeromq number of ZeroMQ threads")
    .define(kafkaTopicConf, Type.STRING, Importance.HIGH, "zeromq Kafka topic to write the messages to")

}