package nl.codestar.kafka.connect.zeromq

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord

import scala.collection.JavaConverters._

object ZeroMQSourceRecord {

  // TODO: convert msg to Array[Byte] using a org.apache.kafka.connect.storage.Converter
  def from(config: ZeroMQSourceConnectorConfig, msg: Object): SourceRecord = {
    val sourcePartition = Map("partition" -> "partition1").asJava
    val sourceOffset = Map("offset" -> "0").asJava
    val topic = config.kafkaTopic
    new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, msg)
  }

}
