package nl.codestar.kafka.connect.zeromq

//import java.io.ByteArrayInputStream
//import java.util.zip.GZIPInputStream

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.zeromq.ZMsg

import scala.collection.JavaConverters._
//import scala.util.Try

object ZeroMQSourceRecord {

  def from(config: ZeroMQSourceConnectorConfig, zmsg: ZMsg): SourceRecord = {
    val sourcePartition = Map("partition" -> "partition1").asJava
    val sourceOffset = Map("offset" -> "0").asJava
    val topic = config.kafkaTopic
    val msgType = zmsg.pop.toString
    val rawContent = zmsg.pop.getData.toString
    new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, msgType, Schema.STRING_SCHEMA, rawContent)
  }

//    val value = Try(unzip(rawContent)).toOption.getOrElse(rawContent.toString)
//  private def unzip(xs: Array[Byte]): String = {
//    val inputStream = new GZIPInputStream(new ByteArrayInputStream(xs))
//    scala.io.Source.fromInputStream(inputStream).mkString
//  }

}
