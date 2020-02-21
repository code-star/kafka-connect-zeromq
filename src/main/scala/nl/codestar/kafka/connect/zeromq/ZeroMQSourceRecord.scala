package nl.codestar.kafka.connect.zeromq

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord

import scala.collection.JavaConverters._

object ZeroMQSourceRecord {

  // TODO: convert msg to Array[Byte] using a org.apache.kafka.connect.storage.Converter
  def from(config: ZeroMQSourceConnectorConfig, msg: Object): SourceRecord = {
    val sourcePartition = Map("routingKey" -> "envelope.getRoutingKey").asJava
    val sourceOffset = Map("deliveryTag" -> "envelope.getDeliveryTag").asJava
    val topic = config.getString(ZeroMQSourceConnectorConfig.kafkaTopicConf)
    new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, msg)
  }

}
