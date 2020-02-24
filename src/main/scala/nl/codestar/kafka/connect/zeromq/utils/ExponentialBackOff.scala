package nl.codestar.kafka.connect.zeromq.utils

import java.time.{Duration, Instant}
import java.util.concurrent.atomic.AtomicInteger

class ExponentialBackOff(step: Duration, cap: Duration) {

  private val iteration: AtomicInteger = new AtomicInteger(0)

  private val endTime: Instant = Instant.now.plus(interval(iteration.get()))

  private def interval(i: Int): Duration = Duration.ofMillis(
    Math.min(
      cap.toMillis,
      step.toMillis * Math.pow(2, i).toLong
    )
  )

  def remaining: Duration = Duration.between(Instant.now, endTime)

  def passed: Boolean = Instant.now isAfter endTime

  def reset(): Unit = iteration.set(0)

  def markFailure(): Int = iteration.incrementAndGet()

}