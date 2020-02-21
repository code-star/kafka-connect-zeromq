package nl.codestar.kafka.connect.zeromq

import java.time.Duration

import com.typesafe.scalalogging.StrictLogging
import nl.codestar.kafka.connect.zeromq.utils.ExponentialBackOff
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.storage.OffsetStorageReader
import org.zeromq.{ZMQ, ZMsg}

import scala.collection.JavaConverters._
import scala.collection.immutable.Queue

class ZeroMQSourceTaskPoller
(
  config: ZeroMQSourceConnectorConfig,
  offsetStorage: OffsetStorageReader,
) extends StrictLogging {

  private val url = config.getString(ZeroMQSourceConnectorConfig.urlConf)
  private val envelopes = config.getList(ZeroMQSourceConnectorConfig.envelopesConf).asScala
  private val nrIoThreads = config.getInt(ZeroMQSourceConnectorConfig.nrIoThreadsConf)

  private val zmqContext = ZMQ.context(nrIoThreads)
  private val zmqConnection = zmqContext.socket(ZMQ.SUB)

  zmqConnection connect url
  envelopes.map(_.getBytes) foreach zmqConnection.subscribe

  def close(): Unit = {
    zmqConnection.close()
    zmqContext.close()
  }

  private val pollDuration: Duration = Duration.parse(config.getString(ZeroMQSourceConnectorConfig.pollIntervalConf))
  private val maxBackoff: Duration = Duration.parse(config.getString(ZeroMQSourceConnectorConfig.maxBackoffConf))
  private var backoff = new ExponentialBackOff(pollDuration, maxBackoff)

  private var buffer = Queue.empty[SourceRecord]

  def poll(): Seq[SourceRecord] = {
    logger.info("polling...")
    val batch = fetchRecords()
    logger.info(s"got ${batch.size} records")
    buffer ++= batch
    val (left, right) = buffer splitAt config.maxPollRecords
    buffer = right
    left
  }

  @scala.annotation.tailrec
  private def fetchRecords(sleepingCounter: Int = 0): Seq[SourceRecord] =
    if (backoff.passed) {
      logger.info("fetching records...")
      receiveAllAvailable()
    } else if (sleepingCounter < 5) {
      logger.info(s"sleeping")
      Thread sleep 1000
      fetchRecords(sleepingCounter + 1)
    } else {
      logger.info("sleep timeout")
      Seq.empty
    }

  @scala.annotation.tailrec
  private def receiveAllAvailable(records: Seq[SourceRecord] = Seq.empty): Seq[SourceRecord] =
    receiveOne() match {
      case None =>
        backoff = backoff.nextFailure()
        logger.info(s"let's backoff ${backoff.remaining}")
        records.reverse
      case Some(record) =>
        receiveAllAvailable(record +: records)
    }

  private def receiveOne(): Option[SourceRecord] =
    for {
      msg <- Option(ZMsg.recvMsg(zmqConnection, ZMQ.DONTWAIT | ZMQ.NOBLOCK))
      _ = logger.info(s"received msg: ${msg.toString}")
      record = ZeroMQSourceRecord.from(config, msg)
    } yield {
      backoff = backoff.nextSuccess()
      record
    }

}
