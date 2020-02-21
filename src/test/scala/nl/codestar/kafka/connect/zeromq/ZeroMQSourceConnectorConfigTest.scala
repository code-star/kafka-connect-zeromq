package nl.codestar.kafka.connect.zeromq

import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConverters._

class ZeroMQSourceConnectorConfigTest
  extends AnyFunSuite {

  test("Minimum configuration") {
    // given
    val settings = TestData.Test1.settings

    // when
    val config = new ZeroMQSourceConnectorConfig(settings)

    // then
    assert(config.getString(ZeroMQSourceConnectorConfig.urlConf) === settings.get(ZeroMQSourceConnectorConfig.urlConf))
    assert(config.getList(ZeroMQSourceConnectorConfig.envelopesConf).asScala.mkString(",") === settings.get(ZeroMQSourceConnectorConfig.envelopesConf))
    assert(config.getString(ZeroMQSourceConnectorConfig.kafkaTopicConf) === settings.get(ZeroMQSourceConnectorConfig.kafkaTopicConf))
  }

}
