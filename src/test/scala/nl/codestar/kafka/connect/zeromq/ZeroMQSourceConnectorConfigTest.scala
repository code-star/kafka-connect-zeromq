package nl.codestar.kafka.connect.zeromq

import org.scalatest.funsuite.AnyFunSuite

class ZeroMQSourceConnectorConfigTest
  extends AnyFunSuite {

  test("Minimum configuration") {
    // given
    val settings = TestData.Test1.settings

    // when
    val config = new ZeroMQSourceConnectorConfig(settings)

    ZeroMQSourceConnectorConfig.definition.defaultValues().get(ZeroMQSourceConnectorConfig.pollIntervalConf)

    // then
    assert(config.url === settings.get(ZeroMQSourceConnectorConfig.urlConf))
    assert(config.envelopes === settings.get(ZeroMQSourceConnectorConfig.envelopesConf))
    assert(config.pollInterval === ZeroMQSourceConnectorConfig.getDefaultString(ZeroMQSourceConnectorConfig.pollIntervalConf))
    assert(config.maxBackoff === ZeroMQSourceConnectorConfig.getDefaultString(ZeroMQSourceConnectorConfig.maxBackoffConf))
    assert(config.maxPollRecords === settings.get(ZeroMQSourceConnectorConfig.maxPollRecordsConf).toInt)
    assert(config.bufferSize === ZeroMQSourceConnectorConfig.getDefaultInt(ZeroMQSourceConnectorConfig.bufferSizeConf))
    assert(config.nrIoThreads === ZeroMQSourceConnectorConfig.getDefaultInt(ZeroMQSourceConnectorConfig.nrIoThreadsConf))
    assert(config.kafkaTopic === settings.get(ZeroMQSourceConnectorConfig.kafkaTopicConf))
  }

}
