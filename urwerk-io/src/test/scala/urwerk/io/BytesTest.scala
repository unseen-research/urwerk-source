package urwerk.io

import java.nio.ByteBuffer

import urwerk.test.TestBase

class BytesTest extends TestBase:
  "wrap entire array" in {
    Bytes.unsafeWrap(bytes(1, 2, 3)) should be(Seq(1, 2, 3))
  }

  "array is wrapped" in {
    val a = bytes(1, 2, 3)
    val bs = Bytes.unsafeWrap(a) 

    a.update(1, 0)
    bs should be(ByteString(1, 0, 3))
  }

  "wrap partial array" in {
    val bs = ByteString.unsafeWrap(bytes(1, 2, 3, 4, 5, 6, 7), 2, 3)
    bs should be(ByteString(3, 4, 5))
  }

  "array segment is unsafe" in {
    val a = bytes(1, 2, 3, 4, 5, 6, 7)
    val bs = ByteString.unsafeWrap(a, 2, 3)

    a.update(1, 0)
    a.update(2, -7)
    bs should be(ByteString(-7, 4, 5))
  }

  "buffer" in {
    val buffer = ByteBuffer.allocate(100)
      .put(Array.tabulate[Byte](42) { _.toByte })
      .flip().position(10)

    ByteString.unsafeWrap(buffer) should be(ByteString(Array.tabulate[Byte](42) { _.toByte }))
  }

  "buffer fail if has no array" in {
    intercept[UnsupportedOperationException] {
      ByteString.unsafeWrap(ByteBuffer.allocate(100).asReadOnlyBuffer())
    }
  }
  
  "remaining buffer fail if has no array" in {
    intercept[UnsupportedOperationException] {
      ByteString.unsafeWrap(ByteBuffer.allocate(100).asReadOnlyBuffer())
    }
  }

  "fail with negative offset" in {
    intercept[IllegalArgumentException] {
      ByteString.unsafeWrap(bytes(1, 2), -1, 5)
    }
  }

  "fail with negative length" in {
    intercept[IllegalArgumentException] {
      ByteString.unsafeWrap(bytes(1, 2), -1, 5)
    }
  }

  "fail with offset exeeded upper bound" in {
    intercept[IndexOutOfBoundsException] {
      ByteString.unsafeWrap(bytes(1, 2), 3, 5)
    }
  }

  "fail with lenth exeeded upper bound" in {
    intercept[IndexOutOfBoundsException] {
      ByteString.unsafeWrap(bytes(1, 2), 0, 5)
    }
  }

  private def bytes(bytes: Byte*) = Array[Byte](bytes: _*)