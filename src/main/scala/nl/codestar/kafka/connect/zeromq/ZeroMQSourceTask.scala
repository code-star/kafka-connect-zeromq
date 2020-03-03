package nl.codestar.kafka.connect.zeromq

import java.util

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}

import scala.collection.JavaConverters._

class ZeroMQSourceTask extends SourceTask with StrictLogging {

  override def version(): String = getClass.getPackage.getImplementationVersion

  private var poller: Option[ZeroMQSourceTaskPoller] = _

  override def start(settings: util.Map[String, String]): Unit = {
    logger.debug("Start task")

    val config = new ZeroMQSourceConnectorConfig(settings)
    poller = Some(new ZeroMQSourceTaskPoller(config, context.offsetStorageReader))
  }

  override def stop(): Unit = synchronized {
    logger.debug("Stop task")
    assert(poller.isDefined)

    poller.foreach(_.close())
  }

  override def poll(): util.List[SourceRecord] = {
    logger.debug("Task poll")
    assert(poller.isDefined)

    poller
      .map(_.poll())
      .getOrElse(Seq.empty)
      .asJava
  }

}
