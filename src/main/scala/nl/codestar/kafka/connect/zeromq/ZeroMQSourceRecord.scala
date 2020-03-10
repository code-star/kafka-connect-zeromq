package nl.codestar.kafka.connect.zeromq

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.zeromq.ZMsg

import scala.collection.JavaConverters._

object ZeroMQSourceRecord {

  def from(config: ZeroMQSourceConnectorConfig, zmsg: ZMsg): SourceRecord = {
    val sourcePartition = Map("partition" -> "1").asJava
    val sourceOffset = Map("offset" -> "0").asJava
    val topic = config.kafkaTopic
    val msgType = zmsg.pop.toString
    val rawContent = zmsg.pop.getData
    new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, msgType, Schema.BYTES_SCHEMA, rawContent)
  }

}
