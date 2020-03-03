package nl.codestar.kafka.connect.zeromq

import com.typesafe.scalalogging.StrictLogging
import org.zeromq.ZMQ

class ZeroMQMockPublisher(timeoutMillisDefault: Long = 10000L, publishIntervalDefault: Long = 50L)
  extends Runnable with StrictLogging {

  val context: ZMQ.Context = ZMQ.context(1)
  val publisher: ZMQ.Socket = context.socket(ZMQ.PUB)

  private val url = TestData.publisherUrl
  publisher.bind(url)

  def publishAll(timeoutMillis: Long = timeoutMillisDefault, publishInterval: Long = publishIntervalDefault): Unit = {
    var timer = 0L
    var msgCount = 0
    while (timer <= timeoutMillis) {
      for {
        envelope <- TestData.Test1.allEnvelopes
        msg = s"[$timer] We inaugurate the evening / Just drumming up a little weirdness"
      } {
        publisher.send(envelope.getBytes(), ZMQ.SNDMORE)
        publisher.send(msg.getBytes())
        msgCount += 1
        logger.info(s"published @ $envelope to $url: $msg")
      }
      Thread.sleep(publishInterval)
      timer += publishInterval
    }
    logger.info("number of messages published: " + msgCount)
  }

  override def run(): Unit = publishAll()

  def close(): Unit = {
    publisher.close()
  }

}
