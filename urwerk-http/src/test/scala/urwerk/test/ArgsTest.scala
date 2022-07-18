package urwerk.test

import com.monovore.decline._
import java.nio.file.Path
import cats.implicits._


class ArgsTest extends TestBase:

  val lines = Opts.option[Int]("lines", short = "n", metavar = "count", help = "Set a number of lines.").orNone

  val file = Opts.argument[Path](metavar = "file").orNone

  val settings = Opts.options[String]("setting", help = "settings help").orEmpty

  val tailOptionsMapped = (lines, file).mapN { (n, files) =>
    println(s"LOG: Printing the last $n lines from each file in $files!")
  }


  val tailOptionsTupled = (lines, file).tupled

  val tailSubcommand = Opts.subcommand("tail", help = "Print the few lines of one or more files.") {
    tailOptionsTupled
  }

  val headOptionsTupled = (lines, file, settings).tupled
  val headSubcommand = Opts.subcommand("head", help = "Print the few lines of one or more files.") {
    headOptionsTupled
  }

  val tailCommand = Command(
    name = "tail",
    header = "Print the last few lines of one or more files."
  ) {
    tailSubcommand orElse headSubcommand
  }



  "tail" in {
    val cmd = tailCommand.parse(Seq("tail", "--lines", "55"))
    println(cmd)
  }

  "head" in {
    //val cmd = tailCommand.parse(Seq("--xx", "x", "path", "--lines", "55"))
    val cmd = tailCommand.parse(Seq("head", "--lines", "55"))
    println(cmd)
    cmd match
    case Left(help) =>
      println(help)
    case Right(cmd) =>
      println(cmd)

  }
