package nl.codestar.kafka.connect.zeromq

import com.typesafe.scalalogging.StrictLogging
import org.zeromq.ZMQ

class ZeroMQMockPublisher extends Runnable with StrictLogging {

  val context: ZMQ.Context = ZMQ.context(1)
  val publisher: ZMQ.Socket = context.socket(ZMQ.PUB)

  private val url = TestData.publisherUrl
  publisher.bind(url)

  private val timeoutDefault = 5000L
  private val publishIntervalDefault = 50L

  def publishAll(timeoutMillis: Long = timeoutDefault, publishInterval: Long = publishIntervalDefault): Unit = {
    var timer = 0L
    while (timer <= timeoutMillis) {
      for {
        envelope <- TestData.Test1.allEnvelopes
        msg = s"[$timer] We inaugurate the evening / Just drumming up a little weirdness"
      } {
        publisher.send(envelope.getBytes(), ZMQ.SNDMORE)
        publisher.send(msg.getBytes())
        logger.info(s"published @ $envelope to $url: $msg")
      }
      Thread.sleep(publishInterval)
      timer += publishInterval
    }
  }

  override def run(): Unit = publishAll()

}
