package nl.codestar.kafka.connect.zeromq

import java.util

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.apache.kafka.connect.source.SourceTaskContext
import org.mockito.{Matchers, Mockito}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar

class ZeroMQSourceTaskTest
  extends AnyFunSuite
    with MockitoSugar
    with StrictLogging {

  private val task = new ZeroMQSourceTask

  private val context = Mockito.mock(classOf[SourceTaskContext])
  private val offsetStorageReader = Mockito.mock(classOf[OffsetStorageReader])
  task.initialize(context)

  Mockito.when(context.offsetStorageReader()).thenReturn(offsetStorageReader)
  Mockito.when(offsetStorageReader.offset(Matchers.any[util.Map[String,Object]])).thenReturn(null)

  private val url = TestData.Test1.url
  private val envelopes = TestData.Test1.envelopes.head
  private val kafkaTopic = TestData.Test1.kafkaTopic

  test("One poll") {
    // given
    val settings = new util.HashMap[String, String]()
    settings.put(ZeroMQSourceConnectorConfig.urlConf, url)
    settings.put(ZeroMQSourceConnectorConfig.envelopesConf, envelopes)
    settings.put(ZeroMQSourceConnectorConfig.kafkaTopicConf, kafkaTopic)

    val publisher = new ZeroMQMockPublisher()
    new Thread(publisher).start()

    // when
    task.start(settings)
    val records = task.poll()

    // then
    assert(records.size() === 20)
  }

}
