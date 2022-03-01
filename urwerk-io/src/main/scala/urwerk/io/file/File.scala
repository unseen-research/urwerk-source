package urwerk.io.file

import urwerk.io.ByteString
import urwerk.source.Sink
import urwerk.source.Source

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.{Files, StandardOpenOption}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.given

trait FileOps {
  extension (file: Path)(using ec: ExecutionContext)
    def byteSource(): Source[ByteString] =
      read(file)

    def byteSource(blockSize: Int): Source[ByteString] =
      read(file, blockSize)
}

given FileOps = new FileOps{}


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



private def read(path: Path)(using ec: ExecutionContext): Source[ByteString] =
  read(path, -1).subscribeOn(ec)

private def read(path: Path, blockSize: Int)(using ec: ExecutionContext): Source[ByteString] =
  def openChannel() =
    AsynchronousFileChannel.open(path, Set(StandardOpenOption.READ).asJava, ec.toExecutorService)

  Source.using(openChannel(), _.close()){channel =>
    Source.create{sink =>
      val bs = if blockSize <=0 then Files.getFileStore(path).getBlockSize.toInt
        else blockSize
      val buffer = ByteBuffer.allocate(bs.toInt)
      channel.read(buffer, 0, buffer, ReadCompletionHandler(channel, sink, 0, blockSize.toInt))
    }
  }

private class ReadCompletionHandler(channel: AsynchronousFileChannel, sink: Sink[ByteString], val position: Long, blockSize: Int) extends CompletionHandler[Integer, ByteBuffer]:
  def completed(readCount: Integer, buffer: ByteBuffer): Unit =
    if readCount >= 0 then
      buffer.flip()
      if buffer.limit() > 0 then
        sink.next(ByteString.from(buffer))
      val nextPos = position + readCount
      channel.read(buffer, nextPos, buffer.clear(),
        ReadCompletionHandler(channel, sink, nextPos, blockSize))
    else
      sink.complete();

  def failed(error: Throwable, buffer: ByteBuffer): Unit =
    sink.error(error)

// val Cwd: io.Path = Path("")
//   .toAbsolutePath.toPath

// val Root = io.Path("/")

// trait GetAttributes[A]:
//   def attributesOf(path: Path): A

// given GetAttributes[BasicFileAttributes] with {
//   def attributesOf(path: io.Path): BasicFileAttributes =
//     Files.getFileAttributeView(Path(path), classOf[BasicFileAttributeView])
//       .readAttributes()
// }

// trait PathOps:
//   extension (path: Path)
//     def bytes(using options: ReadOptions): Source[ByteString] =
//       readBytes(path, options)

//     def attributes[A](using getOp: GetAttributes[A]): Singleton[A] =
//       Singleton.defer(
//         Singleton(getOp.attributesOf(path)))

//     def strings(using codec: Codec, options: ReadOptions): Source[String] =
//       bytes.map(_.mkString)

//     def isFile: Boolean = Files.isRegularFile(Path(path))

//     def isDirectory: Boolean = Files.isDirectory(Path(path))

//     def list: Source[Path] = Source.create{sink =>
//       val stream = Files.list(Path(path))
//         .onClose(() => onDirectoryStreamClose(path))

//       val iterator = stream.iterator.asScala
//       sink.onDispose(stream.close())
//       sink.onRequest{requested =>

//         var remaining = requested

//         while remaining > 0 && iterator.hasNext do
//           remaining -= 1
//           sink.next(
//             iterator.next().toPath)

//         if !iterator.hasNext then
//           sink.complete()
//           stream.close()
//       }
//     }

//     def directories: Source[io.Path] =
//       list.filter(_.isDirectory)

//     def files: Source[io.Path] =
//       list.filter(_.isFile)

//   private def readBytes(path: io.Path, options: ReadOptions): Source[ByteString] =
//     Source.create[ByteString]{sink =>
//       val fileChan = FileChannel.open(Path(path), StandardOpenOption.READ)
//       sink.onRequest(requestCount =>
//         readBytes(fileChan, requestCount, sink, options))
//         .onDispose(
//           fileChan.close())
//     }

//   @tailrec
//   private def readBytes(
//       channel: ReadableByteChannel,
//       requestCount: Long,
//       sink: Sink[ByteString],
//       options: ReadOptions): Unit =
//     if channel.isOpen && requestCount > 0 then
//       val buffer: ByteBuffer = ByteBuffer.allocate(options.chunkSize)
//       val size = readChannel(channel, buffer)
//       if size < 0 then {
//         sink.complete()
//         ()
//       } else
//         buffer.flip()
//         if (buffer.limit() > 0) {
//           sink.next(ByteString.from(buffer))
//         }
//         readBytes(channel, requestCount - 1, sink, options)
//     else
//       ()

//   private[file] def onDirectoryStreamClose(path: io.Path): Unit = {}

//   private[file] def readChannel(channel: ReadableByteChannel, buffer: ByteBuffer): Int =
//     channel.read(buffer)

// given PathOps with {}

// extension (source: Source[io.Path])
//   def zipWithAttributes[A](using getOp: GetAttributes[A]): Source[(io.Path, A)] =
//     source.map(path => (path, getOp.attributesOf(path)))

//////////////////
