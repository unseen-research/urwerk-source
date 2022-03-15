package urwerk.io

import java.nio.ByteBuffer
import scala.collection.immutable.ArraySeq

object ByteSeq:

  def unsafeWrap(array: Array[Byte]): Seq[Byte] = 
    ArraySeq.unsafeWrapArray(array)  

  def unsafeWrap(array: Array[Byte], offset: Int, length: Int): Seq[Byte] = 
    ByteString.unsafeWrap(array, offset, length)
    
  def attemptUnsafeWrap(buffer: ByteBuffer): Seq[Byte] | ByteBuffer = 
    if buffer.hasArray && buffer.capacity == buffer.remaining then
      ArraySeq.unsafeWrapArray(buffer.array)
    else
      buffer
