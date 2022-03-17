package urwerk.io.file

import urwerk.io

import urwerk.source.test.SourceVerifier
import urwerk.test.{TestBase, uniqueFile, uniquePath}

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.StandardOpenOption

import scala.concurrent.ExecutionContext
import scala.util.Random
import scala.collection.compat.immutable.ArraySeq

given ExecutionContext = ExecutionContext.global

class FileTest extends TestBase:

  "create byte source with filesystem block size" in {
    val file = uniqueFile
    val blockSize: Int = Files.getFileStore(file).getBlockSize.toInt
    
    val givenBlocks = for i <- 0 to 3 
      yield Random.nextBytes(blockSize)

    givenBlocks.foreach{block =>
      Files.write(file, block, StandardOpenOption.APPEND)
    }

    val actualBlocks = file.byteSource()
      .toSeq.block

    actualBlocks should be(givenBlocks.map(_.toSeq))
  }

  "create byte source with custom block size" in {
    val givenBytes = Random.nextBytes(2)

    val givenBuffers = givenBytes.map(byte => ArraySeq.unsafeWrapArray(Array(byte)))
    val file = uniqueFile(givenBytes)

    val actualBuffers = file.byteSource(1)
    .toSeq.block
    actualBuffers should be (givenBuffers)
  }

  "create byte source fails with no such file" in {
    SourceVerifier(
        uniquePath.byteSource())
      .expectError(classOf[NoSuchFileException])
      .verify()
  }


  // "current working directory" in {
  //   Cwd.absolute should be (true)
  //   Path(sys.props("user.dir")) should be (Cwd)
  // }

  // Seq(
  //   (io.Path(""), Path("")),
  //   (io.Path("/"), Path("/")),
  //   (io.Path("/abc"), Path("/abc")),
  //   (io.Path("abc/def"), Path("abc/def")))
  //   .foreach{(givenIoPath, givenFilePath) =>
  //     val suffix = s"from='$givenIoPath' to='$givenFilePath'"

  //     s"from io.Path to file.Path $suffix" in {
  //       val path: Path = Path(givenIoPath)
  //       path should be (givenFilePath)
  //       path.isAbsolute should be (givenIoPath.absolute)
  //     }

  //     s"from file.Path to ioPath $suffix" in {
  //       val path: io.Path = givenFilePath.toPath
  //       path should be (givenIoPath)
  //       path.absolute should be (givenFilePath.isAbsolute)
  //     }
  //   }

  // "root" in {
  //   Root.absolute should be (true)
  //   Root should be (Path("/"))
  // }

  // "attributes" in {
  //   singletonProbe(uniqueFile(Array[Byte](1, 2, 3))
  //       .attributes[BasicFileAttributes])
  //     .assertNext(attrs =>
  //       attrs.size should be (3)
  //       attrs.isRegularFile should be(true)
  //     )
  // }

  // "path by string" in {
  //   io.Path("abc", "def/ghi").elements.map(_.toString) should be(Seq("abc", "def", "ghi"))
  // }

  // "file bytes" in {
  //   val givenBytes = Random.nextBytes(10)
  //   val file = uniqueFile(givenBytes)

  //   file.bytes.reduce(_ ++ _).block.get should be(ByteString(givenBytes))
  // }



  // "file bytes fail with io exception on read" in {
  //   val file = uniqueFile(Array[Byte](1, 2, 3))
  //   given PathOps with {
  //     override private[file] def readChannel(channel: ReadableByteChannel, buffer: ByteBuffer): Int =
  //       throw IllegalStateException()
  //   }

  //   sourceProbe(
  //     file.bytes)
  //     .expectError(classOf[IllegalStateException])
  //     .verify()
  // }

  // "is file" in {
  //   uniqueFile.isFile should be (true)
  //   uniqueDirectory.isFile should be (false)
  // }

  // "is directory" in {
  //   uniqueFile.isDirectory should be (false)
  //   uniqueDirectory.isDirectory should be (true)
  // }
  // "list" - {
  //   val dir = uniqueDirectory
  //   val givenPaths = Set[Path](
  //     Files.createDirectory(Path(dir / "dir-1")),
  //     Files.createFile(Path(dir / "file-1")),
  //     Files.createDirectory(Path(dir / "dir-2")),
  //     Files.createFile(Path(dir / "file-2")))

  //   "pathes" in {
  //     var closed = false
  //     given PathOps with {
  //       override private[file] def onDirectoryStreamClose(path: io.Path): Unit =
  //         closed = true
  //     }

  //     val actualPaths = dir.list.toSeq.block
  //     actualPaths.toSet should be(givenPaths)
  //     closed should be(true)
  //   }
  //   "directories" in {
  //     val actualDirs = dir.directories.toSeq.block.toSet
  //     actualDirs should be(givenPaths.filter(Files.isDirectory(_)))
  //   }
  //   "files" in {
  //     val actualFiles = dir.files.toSeq.block.toSet
  //     actualFiles should be(givenPaths.filter(Files.isRegularFile(_)))
  //   }
  // }

  // "strings" in {
  //   given ReadOptions with {
  //     val chunkSize = 2
  //   }
  //   val file = uniqueFile("abc")
  //   sourceProbe(
  //       file.strings)
  //     .expectNext("ab", "c")
  //     .verifyComplete()
  // }

  // "zip with attributes" in {
  //   val givenSrc = Seq(uniqueFile, uniqueDirectory, uniqueFile, uniqueDirectory)
  //   val pathsWithAttrs = Source.from(givenSrc)
  //     .zipWithAttributes[BasicFileAttributes]
  //     .map((path: io.Path, attrs: BasicFileAttributes) => (path, attrs.isRegularFile, attrs.isDirectory))
  //     .toSeq.block

  //   val givenPathAttrs = givenSrc.map(path => (path, Files.isRegularFile(Path(path)), Files.isDirectory(Path(path))))
  //   pathsWithAttrs should be(givenPathAttrs)
  // }
