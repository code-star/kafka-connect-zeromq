package nl.codestar.kafka.connect.zeromq

import java.time.Duration

import com.typesafe.scalalogging.StrictLogging
import nl.codestar.kafka.connect.zeromq.utils.ExponentialBackOff
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.zeromq.{ZMQ, ZMsg}

import scala.collection.mutable
import scala.util.Try

class ZeroMQSourceTaskPoller
(
  config: ZeroMQSourceConnectorConfig,
  offsetStorage: OffsetStorageReader,
  socketType: Int = ZMQ.SUB,
) extends StrictLogging {

  private val zmqContext = ZMQ.context(config.nrIoThreads)
  private val zmqConnection = zmqContext.socket(socketType)

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
  private val sleepingTime = pollDuration.toMillis

  def poll(): Seq[SourceRecord] = {
    logger.debug("polling...")
    buffer ++= fetchRecords()

    var left = Seq.empty[SourceRecord]
    while (buffer.nonEmpty && left.size < config.maxPollRecords) {
      left +:= buffer.dequeue()
    }
    left
  }

  private def fetchRecords(sleepingCounter: Int = 0): Seq[SourceRecord] =
    if (backoff.hasPassed) {
      logger.debug("fetching records...")
      val records = Stream.continually(receiveOne()).takeWhile(_.isDefined).flatten
      logger.info(s"received ${records.size} records")
      if (records.isEmpty) {
        backoff.backoff()
        val delta = Try{backoff.remaining.toMillis}.toOption.getOrElse(0L)
        if (delta > 0L)
          logger.info(s"let's wait ${delta}ms until next poll")
      }
      records
    } else {
      logger.debug(s"waiting $sleepingTime ms for next poll")
      Thread sleep sleepingTime
      Seq.empty
    }

  private def receiveOne(): Option[SourceRecord] =
    for {
      zmsg <- Option(ZMsg.recvMsg(zmqConnection, ZMQ.DONTWAIT | ZMQ.NOBLOCK))
      record = ZeroMQSourceRecord.from(config, zmsg)
    } yield {
      logger.debug(s"received record: ${record.toString}")
      backoff.reset()
      record
    }

}
