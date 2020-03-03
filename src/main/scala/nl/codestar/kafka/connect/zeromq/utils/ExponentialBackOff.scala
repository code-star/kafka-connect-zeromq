package nl.codestar.kafka.connect.zeromq.utils

import java.time.{Duration, Instant}
import java.util.concurrent.atomic.AtomicInteger

class ExponentialBackOff(step: Duration, cap: Duration) {

  private var initTime = Instant.now

  private val iteration: AtomicInteger = new AtomicInteger(0)

  private def interval(i: Int): Duration = Duration.ofMillis(
    Math.min(
      cap.toMillis,
      step.toMillis * Math.pow(2, i).toLong
    )
  )

  def endTime(): Instant = initTime plus interval(iteration.get())

  def hasPassed: Boolean = Instant.now isAfter endTime()

  def reset(): Unit = synchronized {
    initTime = Instant.now
    iteration.set(0)
  }

  def backoff(): Int = iteration.incrementAndGet()

  def remaining: Duration = Duration.between(Instant.now, endTime())

}