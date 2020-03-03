package nl.codestar.kafka.connect.zeromq

import java.time.{Duration, Instant}

import com.typesafe.scalalogging.StrictLogging
import nl.codestar.kafka.connect.zeromq.utils.ExponentialBackOff
import org.scalatest.funsuite.AnyFunSuite

class ExponentialBackOffTest extends AnyFunSuite with StrictLogging {

  test("Basic properties") {
    val step = Duration.parse("PT0.1S")
    val cap = Duration.parse("PT5S")
    val now = Instant.now
    val backoff = new ExponentialBackOff(step, cap)

    val end = backoff.endTime()
    val interval0 = Duration.ofMillis(step.toMillis)
    val end0 = now plus interval0
    assert(end === end0 || (end isAfter end0))
    assert(now isBefore end)
    assert(!backoff.hasPassed)

    backoff.backoff()
    assert(!backoff.hasPassed)
    assert((backoff.remaining compareTo cap) == -1)
  }

  test("Many backoffs followed by reset") {
    val step = Duration.parse("PT0.1S")
    val cap = Duration.parse("PT5S")

    val backoff = new ExponentialBackOff(step, cap)
    val t0 = Instant.now
    // now = t0 -- end = t0 + step
    assert(!backoff.hasPassed)

    Thread.sleep(step.toMillis)
    // now >= t0 + step -- end: t0 + step
    assert(backoff.hasPassed || backoff.endTime() === (t0 plus step))

    backoff.backoff()
    // now >= t0 + step -- end: t0 + step * 2
    assert(!backoff.hasPassed)

    Thread.sleep(step.toMillis)
    // now >= t0 + step * 2 -- end: t0 + step * 2
    assert(backoff.hasPassed)

    backoff.backoff()
    // now >= t0 + step * 2 -- end: t0 + step * 4
    assert(!backoff.hasPassed)

    backoff.backoff()
    // now >= t0 + step * 2 -- end: t0 + step * 8
    assert(!backoff.hasPassed)

    backoff.reset()
    val t1 = Instant.now
    // now = t1 -- end: t1 + step
    assert(!backoff.hasPassed)

    Thread.sleep(step.toMillis)
    // now >= t1 + step -- end: t1 + step
    assert(backoff.hasPassed || backoff.endTime() === (t1 plus step))
  }

//  test("Run simulation") {
//    val sleepingTime = 1000L
//    val step = Duration.parse("PT0.1S")
//    val cap = Duration.parse("PT2S")
//
//    val backoff = new ExponentialBackOff(step, cap)
//
//    for (i <- 1 to 20) {
//      logger.info(s"iteration: $i")
//      if (backoff.hasPassed) {
//        logger.info("endTime has passed")
//        if (i < 10 || i > 15) {
//          backoff.backoff()
//          val delta = backoff.remaining.toMillis
//          if (delta > 0)
//            logger.info(s"let's wait ${backoff.remaining.toMillis}ms until next iteration")
//          else
//            logger.info(s"ahead of end time by ${delta * -1}ms")
//        } else {
//          logger.info("iteration 11 .. 14 reached!")
//          backoff.reset()
//        }
//      } else {
//        logger.info("endTime has not passed")
//        logger.info(s"waiting ${sleepingTime}ms for next iteration")
//        Thread sleep sleepingTime
//      }
//      Thread sleep 100
//    }
//  }

}
