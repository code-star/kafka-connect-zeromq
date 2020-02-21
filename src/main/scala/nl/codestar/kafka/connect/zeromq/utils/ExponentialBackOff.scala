package nl.codestar.kafka.connect.zeromq.utils

import java.time.{Duration, Instant}

// TODO: make it concurrent
class ExponentialBackOff(step: Duration, cap: Duration, iteration: Int = 0) {

  private val endTime: Instant = Instant.now.plus(interval(iteration))

  private def interval(i: Int): Duration = Duration.ofMillis(
    Math.min(
      cap.toMillis,
      step.toMillis * Math.pow(2, i).toLong
    )
  )

  def remaining: Duration = Duration.between(Instant.now, endTime)

  def passed: Boolean = Instant.now isAfter endTime

  def nextSuccess(): ExponentialBackOff = new ExponentialBackOff(step, cap)

  def nextFailure(): ExponentialBackOff = new ExponentialBackOff(step, cap, iteration + 1)

}