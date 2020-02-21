package nl.codestar.kafka.connect.zeromq

import java.util

import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

import scala.collection.JavaConverters._

class ZeroMQSourceConnectorConfig(settings: util.Map[String, String])
  extends AbstractConfig(ZeroMQSourceConnectorConfig.definition, settings) {

  def url: String = getString(ZeroMQSourceConnectorConfig.urlConf)
  def envelopesList: List[String] = getList(ZeroMQSourceConnectorConfig.envelopesConf).asScala.toList
  def envelopes: String = envelopesList.mkString(",")
  def pollInterval: String = getString(ZeroMQSourceConnectorConfig.pollIntervalConf)
  def maxBackoff: String = getString(ZeroMQSourceConnectorConfig.maxBackoffConf)
  def maxPollRecords: Integer = getInt(ZeroMQSourceConnectorConfig.maxPollRecordsConf)
  def bufferSize: Integer = getInt(ZeroMQSourceConnectorConfig.bufferSizeConf)
  def nrIoThreads: Integer = getInt(ZeroMQSourceConnectorConfig.nrIoThreadsConf)
  def kafkaTopic: String = getString(ZeroMQSourceConnectorConfig.kafkaTopicConf)
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

  private def getDefault(keyConf: String): AnyRef = ZeroMQSourceConnectorConfig.definition.defaultValues().get(keyConf)
  def getDefaultString(keyConf: String): String = getDefault(keyConf).asInstanceOf[String]
  def getDefaultInt(keyConf: String): Integer = getDefault(keyConf).asInstanceOf[Integer]
  def getDefaultList(keyConf: String): List[String] = getDefault(keyConf).asInstanceOf[List[String]]
}