package nl.codestar.kafka.connect.zeromq

import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.apache.kafka.connect.connector.ConnectorContext
import org.mockito.Mockito

import scala.collection.JavaConverters._

class ZeroMQSourceConnectorTest
  extends AnyFunSuite with MockitoSugar {

  private val connector = new ZeroMQSourceConnector
  private val ctx = Mockito.mock(classOf[ConnectorContext])
  connector.initialize(ctx)

  private val settings = TestData.Test1.settings

  test("Validate configuration") {
    for (validatedValue <- connector.config().validate(settings).asScala)
      assert(validatedValue.errorMessages.size === 0,
        "Config property errors: " + validatedValue.errorMessages
      )
  }

  test("Connector source tasks") {
    // given
    connector.start(settings)

    // when
    val taskConfigs = connector.taskConfigs(1)

    // then
    assert(taskConfigs.size === 1)
    assert(taskConfigs.get(0).get(ZeroMQSourceConnectorConfig.urlConf) === TestData.Test1.url)
    assert(taskConfigs.get(0).get(ZeroMQSourceConnectorConfig.envelopesConf) === TestData.Test1.subscribedEnvelopes)
  }

}
