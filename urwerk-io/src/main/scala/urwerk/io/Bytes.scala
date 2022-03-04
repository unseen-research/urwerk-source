package urwerk.io

import java.nio.ByteBuffer

object Bytes:

  def unsafeWrap(array: Array[Byte]): Seq[Byte] = 
    ByteString.unsafeWrap(array)  

  def unsafeWrap(array: Array[Byte], offset: Int, length: Int): Seq[Byte] = 
    ByteString.unsafeWrap(array, offset, length)
    
  def unsafeWrap(buffer: ByteBuffer): Seq[Byte] = 
    ByteString.unsafeWrap(buffer)