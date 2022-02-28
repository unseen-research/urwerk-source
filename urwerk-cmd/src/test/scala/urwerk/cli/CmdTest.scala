package urwerk.cli

import urwerk.test.TestBase
import Cmd.*

class CmdTest extends TestBase:
  
  "bind values" in {
    case class Config(stringField: String)
    val cmd = Cmd[Config](
      bind / "stringField" := "stringValue"
    )

    cmd.execute() should be (0)
  }
