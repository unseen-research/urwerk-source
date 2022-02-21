package urwerk.source.internal

import java.util.concurrent.{ AbstractExecutorService, TimeUnit }
import java.util.Collections

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.ExecutionContextExecutorService

extension (executionContext: ExecutionContext)
  def toExecutor: ExecutionContextExecutor = toExecutorService

  def toExecutorService: ExecutionContextExecutorService = executionContext match
    case null => throw null
    case ec: ExecutionContextExecutorService => ec
    case ec => new AbstractExecutorService with ExecutionContextExecutorService {
      override def prepare(): ExecutionContext = ec
      override def isShutdown = false
      override def isTerminated = false
      override def shutdown() = ()
      override def shutdownNow() = Collections.emptyList[Runnable]
      override def execute(runnable: Runnable): Unit = ec.execute(runnable)
      override def reportFailure(t: Throwable): Unit = ec.reportFailure(t)
      override def awaitTermination(length: Long,unit: TimeUnit): Boolean = false
    }
