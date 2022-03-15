package urwerk.io

import java.nio.ByteBuffer
import scala.collection.immutable.ArraySeq

object ByteSeq:

  def unsafeWrap(array: Array[Byte]): Seq[Byte] = 
    ArraySeq.unsafeWrapArray(array)  

  def unsafeWrap(array: Array[Byte], offset: Int, length: Int): Seq[Byte] = 
    ByteString.unsafeWrap(array, offset, length)
    
  def unsafeWrap(buffer: ByteBuffer): Seq[Byte] = 
    ByteString.unsafeWrap(buffer)