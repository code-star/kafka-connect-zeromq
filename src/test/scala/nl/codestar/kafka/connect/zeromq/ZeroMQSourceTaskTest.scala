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

  test("One poll") {
    // given
    val publisher = new ZeroMQMockPublisher()
    new Thread(publisher).start()

    val settings = TestData.Test1.settings
    val config = TestData.Test1.config

    // when
    task.start(settings)
    val records = task.poll()

    // then
    records forEach { record =>
      assert(record.topic() === TestData.Test1.kafkaTopic)
    }
    assert(records.size() <= config.maxPollRecords)
  }

}
