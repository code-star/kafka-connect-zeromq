package nl.codestar.kafka.connect.zeromq

import java.time.{Duration, Instant}

import com.typesafe.scalalogging.StrictLogging
import nl.codestar.kafka.connect.zeromq.utils.ExponentialBackOff
import org.scalatest.funsuite.AnyFunSuite

class ExponentialBackOffTest extends AnyFunSuite with StrictLogging {

  test("Basic properties") {
    val step = Duration.parse("PT0.1S")
    val cap = Duration.parse("PT5S")
    val now = Instant.now.toEpochMilli
    val backoff = new ExponentialBackOff(step, cap)

    val end = backoff.endTime()
    val interval0 = step.toMillis
    val end0 = now + interval0
    assert(end === end0 || (end compareTo end0) > 0)
    assert((now compareTo end) < 0)
    assert(backoff.remainingMillis >= 0)

    backoff.backoff()
    assert(backoff.remainingMillis >= 0)
    assert((backoff.remainingMillis compareTo cap.toMillis) < 0)
  }

  test("Many backoffs followed by reset") {
    val step = Duration.parse("PT0.1S")
    val cap = Duration.parse("PT5S")

    val backoff = new ExponentialBackOff(step, cap)
    val t0 = Instant.now
    // now = t0, end = t0 + step
    assert(backoff.remainingMillis >= 0)

    Thread.sleep(step.toMillis)
    // now >= t0 + step, end: t0 + step
    assert(backoff.remainingMillis < 0 || backoff.endTime() === (t0 plus step).toEpochMilli)

    backoff.backoff()
    // now >= t0 + step, end: t0 + step * 2
    assert(backoff.remainingMillis >= 0)

    Thread.sleep(step.toMillis)
    // now >= t0 + step * 2, end: t0 + step * 2
    assert(backoff.remainingMillis < 0)

    backoff.backoff()
    // now >= t0 + step * 2, end: t0 + step * 4
    assert(backoff.remainingMillis >= 0)

    backoff.backoff()
    // now >= t0 + step * 2, end: t0 + step * 8
    assert(backoff.remainingMillis >= 0)

    backoff.resetAll()
    val t1 = Instant.now
    // now = t1, end: t1 + step
    assert(backoff.remainingMillis >= 0)

    Thread.sleep(step.toMillis)
    // now >= t1 + step, end: t1 + step
    assert(backoff.remainingMillis < 0 || backoff.endTime() === (t1 plus step).toEpochMilli)
  }

  test("End time is positive and less or equal than the cap") {
    val step = Duration.parse("PT0.1S")
    val cap = Duration.parse("PT2S")

    val backoff = new ExponentialBackOff(step, cap)
    val t0 = Instant.now.toEpochMilli

    for (_ <- 0 to 200) {
      backoff.backoff()
      val end = backoff.endTime() - t0
      val max = cap.toMillis
      assert(end > 0)
      assert(end <= max)
    }
  }

//  test("Run simulation") {
//    val step = Duration.parse("PT0.1S")
//    val cap = Duration.parse("PT2S")
//
//    val backoff = new ExponentialBackOff(step, cap)
//
//    for (i <- 1 to 20) {
//      logger.info(s"polling #$i")
//      logger.info(backoff.toString)
//      if (backoff.remainingMillis < 0) {
//        logger.info("  endTime has passed: do something!")
//        logger.info(s"  ${backoff.toString}")
//        backoff.resetInit()
//        if (i < 10 || i > 15) {
//          logger.info("    no records: backoff!")
//          backoff.backoff()
//          val delta = backoff.remaining.toMillis
//          if (delta > 0)
//            logger.info(s"    let's wait ${backoff.remaining.toMillis}ms until next poll")
//          else
//            logger.info(s"    ahead of end time by ${delta * -1}ms")
//          logger.info(s"    ${backoff.toString}")
//        } else {
//          logger.info("    records found! (iteration 11 .. 14 reached!) --> reset()")
//          backoff.reset()
//        }
//      } else {
//        logger.info("  endTime has not passed: wait.")
//        logger.info(s"  waiting ${backoff.remaining.toMillis}ms for next poll")
//        logger.info(s"  ${backoff.toString}")
//        Thread sleep backoff.remaining.toMillis
//      }
//      Thread sleep 100
//    }
//  }

}
