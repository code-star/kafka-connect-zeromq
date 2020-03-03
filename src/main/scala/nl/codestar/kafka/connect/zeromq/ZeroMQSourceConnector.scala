package nl.codestar.kafka.connect.zeromq

import java.util

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceConnector

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

class ZeroMQSourceConnector extends SourceConnector with StrictLogging {

  override def taskClass(): Class[_ <: Task] = classOf[ZeroMQSourceTask]

  override def config(): ConfigDef = ZeroMQSourceConnectorConfig.definition

  override def version(): String = getClass.getPackage.getImplementationVersion

  private var maybeSettings: Option[util.Map[String, String]] = None

  override def start(settings: util.Map[String, String]): Unit = {
    logger.debug("Start connector")
    maybeSettings = Some(settings)
    Try(new ZeroMQSourceConnectorConfig(settings)) match {
      case Failure(f) => throw new ConnectException("Couldn't start due to configuration error: " + f.getMessage, f)
      case _ => ()
    }
  }

  override def stop(): Unit =
    logger.debug("Stop connector")

  // Note that only one task maximum is used.
  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    logger.debug(s"Setting task configurations for $maxTasks workers")
    assert(maxTasks >= 0)
    assert(maybeSettings.isDefined, "Connector is not initialized: cannot start tasks")

    val taskSettings = for {
      settings <- maybeSettings.toList
      _ <- 1 to Math.max(maxTasks, 1)
    } yield settings

    taskSettings.asJava
  }

}
