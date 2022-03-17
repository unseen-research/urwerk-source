package urwerk.io.file

import java.util.concurrent.{ AbstractExecutorService, TimeUnit }
import java.util.Collections
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.{Files, StandardOpenOption}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.ExecutionContextExecutorService
import scala.jdk.CollectionConverters.given

import urwerk.source.Sink
import urwerk.source.Source
import scala.collection.compat.immutable.ArraySeq
import scala.languageFeature.postfixOps

extension (file: Path)(using ec: ExecutionContext)
  def bytes: Source[Seq[Byte]] =
    read(file)

  def bytes(blockSize: Int): Source[Seq[Byte]] =
    read(file, blockSize)

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

private def read(file: Path)(using ec: ExecutionContext): Source[Seq[Byte]] =
  read(file, -1).subscribeOn(ec)

private def read(file: Path, blockSize: Int)(using ec: ExecutionContext): Source[Seq[Byte]] =
  def openChannel() =
    AsynchronousFileChannel.open(file, Set(StandardOpenOption.READ).asJava, ec.toExecutorService)

  Source.using(openChannel(), _.close()){channel =>
    Source.create{sink =>
      val bs = if blockSize <=0 then Files.getFileStore(file).getBlockSize.toInt
        else blockSize
      
      val buffer = ByteBuffer.allocate(bs.toInt)
      read(channel, sink, 0, buffer)
    }
  }

private def read(channel: AsynchronousFileChannel, sink: Sink[Seq[Byte]], position: Long, buffer: ByteBuffer): Unit = 
  buffer.clear()
  channel.read(buffer, position, buffer, new CompletionHandler:
    def completed(readCount: Integer, buffer: ByteBuffer): Unit =
      if readCount < 0 then
        sink.complete()
        
      else
        buffer.flip()
        val bytes = Array.ofDim[Byte](buffer.remaining)
        buffer.get(bytes)
        sink.next(ArraySeq.unsafeWrapArray(bytes))
        read(channel, sink, position + readCount, buffer)

    def failed(error: Throwable, buffer: ByteBuffer): Unit =
      sink.error(error)
  )
