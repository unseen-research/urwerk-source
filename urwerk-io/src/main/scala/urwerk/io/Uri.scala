package urwerk.io

import java.net.URI

type Uri = URI

class UURRII
object Uri {
  def apply(uri: String): Uri = {
    new Uri(uri)
  }

  extension (uri: Uri) {
    def path: Path = Path(uri.getPath)
  }
}
