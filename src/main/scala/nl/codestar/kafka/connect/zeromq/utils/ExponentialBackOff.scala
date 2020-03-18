package nl.codestar.kafka.connect.zeromq.utils

import java.time.{Duration, Instant}
import java.util.concurrent.atomic.AtomicInteger

class ExponentialBackOff(step: Duration, cap: Duration) {

  private var initTime = Instant.now.toEpochMilli

  private val iteration: AtomicInteger = new AtomicInteger(0)

  private def interval(i: Int): Long = {
    val d = BigInt(step.toMillis) * BigInt(2).pow(i)
    scala.math.min(cap.toMillis, if (d.isValidLong) d.toLong else Long.MaxValue)
  }

  def endTime(): Long = initTime + interval(iteration.get())

  def remainingMillis: Long = endTime() - Instant.now.toEpochMilli

  def resetInit(): Unit = synchronized {
    initTime = Instant.now.toEpochMilli
  }

  def resetAll(): Unit = synchronized {
    resetInit()
    iteration.set(0)
  }

  def backoff(): Int = iteration.incrementAndGet()

  def remaining: Duration = Duration.between(Instant.now, endTime())

}