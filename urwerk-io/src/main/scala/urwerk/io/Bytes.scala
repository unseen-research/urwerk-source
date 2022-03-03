package urwerk.io

import java.nio.ByteBuffer

object Bytes:

  def bind(array: Array[Byte]): Seq[Byte] = 
    ByteString.unsafeWrap(array)  

  def bind(array: Array[Byte], offset: Int, length: Int): Seq[Byte] = 
    ByteString.unsafeWrap(array, offset, length)
    
  def bind(buffer: ByteBuffer): Seq[Byte] = 
    ByteString.unsafeWrap(buffer)