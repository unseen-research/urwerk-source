package urwerk.io

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.Channels

import scala.annotation.tailrec

import urwerk.source.Source
import urwerk.source.Sink
import java.io.OutputStream

object Streams:
  val DefaultBufferSize: Int = 4096 * 2

  extension [A <: OutputStream](outputStream: A)
    def write(bytes: Seq[Byte]): A =
      for(i <- 0 until bytes.size) 
        outputStream.write(bytes(i))
      
      outputStream

  extension (inputStream: InputStream)
    def toSource: Source[Seq[Byte]] =
      toSource(Streams.DefaultBufferSize)

    def toSource(blockSize: Int): Source[Seq[Byte]] = readBytes(inputStream, blockSize)

private def readBytes(inputStream: InputStream, blockSize: Int): Source[Seq[Byte]] =
  Source.using(Channels.newChannel(inputStream), _.close){channel =>
    Source.create[Seq[Byte]]{sink =>
      val buffer: ByteBuffer = ByteBuffer.allocate(blockSize)
      readBytes(channel, buffer, sink, blockSize)
    }
  }

@tailrec
private def readBytes(
    channel: ReadableByteChannel,
    buffer: ByteBuffer,
    sink: Sink[Seq[Byte]],
    blockSize: Int): Unit =
  val size = channel.read(buffer)

  if size < 0 then
    sink.complete()
  else
    buffer.flip()
    if (buffer.limit() > 0) {
      sink.next(ByteString.from(buffer))
    }
    readBytes(channel, buffer.clear(), sink, blockSize)
