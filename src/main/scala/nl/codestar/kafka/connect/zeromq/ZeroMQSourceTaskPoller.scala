package nl.codestar.kafka.connect.zeromq

import java.time.Duration

import com.typesafe.scalalogging.StrictLogging
import nl.codestar.kafka.connect.zeromq.utils.ExponentialBackOff
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.zeromq.{ZMQ, ZMsg}

import scala.collection.mutable

class ZeroMQSourceTaskPoller
(
  config: ZeroMQSourceConnectorConfig,
  offsetStorage: OffsetStorageReader,
) extends StrictLogging {

  private val zmqContext = ZMQ.context(config.nrIoThreads)
  private val zmqConnection = zmqContext.socket(ZMQ.SUB)

  zmqConnection connect config.url
  config.envelopesList map (_.getBytes) foreach zmqConnection.subscribe

  def close(): Unit = {
    zmqConnection.close()
    zmqContext.close()
  }

  private val pollDuration: Duration = Duration.parse(config.pollInterval)
  private val maxBackoff: Duration = Duration.parse(config.maxBackoff)
  private val backoff = new ExponentialBackOff(pollDuration, maxBackoff)

  private val buffer = mutable.Queue.empty[SourceRecord]
  private val sleppingTime = 1000L
  private val maxSleepingTimes = 5

  def poll(): Seq[SourceRecord] = {
    logger.info("polling...")
    buffer ++= fetchRecords()

    var left = Seq.empty[SourceRecord]
    while (buffer.nonEmpty && left.size < config.maxPollRecords) {
      left +:= buffer.dequeue()
    }
    left
  }

  @scala.annotation.tailrec
  private def fetchRecords(sleepingCounter: Int = 0): Seq[SourceRecord] =
    if (backoff.passed) {
      logger.info("fetching records...")
      val records = Stream.continually(receiveOne()).takeWhile(_.isDefined).flatten
      logger.info(s"got ${records.size} records")
      if (records.isEmpty) {
        backoff.markFailure()
        logger.info(s"let's backoff ${backoff.remaining}")
      }
      records
    } else if (sleepingCounter < maxSleepingTimes) {
      logger.info(s"sleeping")
      Thread sleep sleppingTime
      fetchRecords(sleepingCounter + 1)
    } else {
      logger.info("sleep timeout")
      Seq.empty
    }

  private def receiveOne(): Option[SourceRecord] =
    for {
      msg <- Option(ZMsg.recvMsg(zmqConnection, ZMQ.DONTWAIT | ZMQ.NOBLOCK))
      record = ZeroMQSourceRecord.from(config, msg)
    } yield {
      logger.info(s"received msg: ${msg.toString}")
      backoff.reset()
      record
    }

}
