package nl.codestar.kafka.connect.zeromq

import java.util

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.connect.source.SourceTaskContext
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.mockito.{Matchers, Mockito}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar

class ZeroMQSourceTaskTest
  extends AnyFunSuite
    with MockitoSugar
    with LazyLogging {

  private val task = new ZeroMQSourceTask

  private val context = Mockito mock classOf[SourceTaskContext]
  private val offsetStorageReader = Mockito mock classOf[OffsetStorageReader]
  task initialize context

  Mockito when context.offsetStorageReader() thenReturn offsetStorageReader
  Mockito when offsetStorageReader.offset(Matchers.any[util.Map[String,Object]]) thenReturn null

  test("No messages") {
    val settings = TestData.Test1.settings

    task.start(settings)

    assert(task.poll().isEmpty)
    assert(task.poll().isEmpty)
    assert(task.poll().isEmpty)
  }

  test("Retrieved records: topic is as configured and size is less than the maximum") {
    // given
    val publisher = new ZeroMQMockPublisher()
    new Thread(publisher).start()
    Thread.sleep(1000)

    val settings = TestData.Test1.settings
    val config = TestData.Test1.config

    // when
    task.start(settings)
    assert(task.poll().isEmpty) // first poll does not fetch any records (backoff.passedEndTime = false)
    val records = task.poll()

    // then
    records forEach { record =>
      assert(record.topic() === TestData.Test1.kafkaTopic)
    }
    assert(records.size() <= config.maxPollRecords)

    // finally
    publisher.close()
  }

//  test("pubsub.besteffort.ndovloket.nl:7664") {
//    val settings = TestData.Test2.settings
//    val config = TestData.Test2.config
//
//    task.start(settings)
//    assert(task.poll().isEmpty) // first poll does not fetch any records (backoff.passedEndTime = false)
//    val records = task.poll()
//
//    records forEach { record =>
//      assert(record.topic() === TestData.Test1.kafkaTopic)
//    }
//    assert(records.size() <= config.maxPollRecords)
//
//    val records2 = task.poll()
//    println("records2: "+records2.toString)
//
//    val records3 = task.poll()
//    println("records3: "+records3.toString)
//  }

}
