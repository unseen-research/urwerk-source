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

import urwerk.io.Bytes
import urwerk.source.Sink
import urwerk.source.Source

extension (file: Path)(using ec: ExecutionContext)
  def byteSource(): Source[Seq[Byte]] =
    read(file)

  def byteSource(blockSize: Int): Source[Seq[Byte]] =
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
      channel.read(buffer, 0, buffer, ReadCompletionHandler(channel, sink, 0, blockSize.toInt))
    }
  }

private class ReadCompletionHandler(channel: AsynchronousFileChannel, sink: Sink[Seq[Byte]], val position: Long, blockSize: Int) extends CompletionHandler[Integer, ByteBuffer]:
  
  def completed(readCount: Integer, buffer: ByteBuffer): Unit =
    if readCount >= 0 then
      buffer.flip()
      if buffer.limit() > 0 then
        sink.next(Bytes.unsafeWrap(buffer))
      val nextPos = position + readCount
      channel.read(buffer, nextPos, buffer.clear(),
        ReadCompletionHandler(channel, sink, nextPos, blockSize))
    else
      sink.complete();

  def failed(error: Throwable, buffer: ByteBuffer): Unit =
    sink.error(error)
