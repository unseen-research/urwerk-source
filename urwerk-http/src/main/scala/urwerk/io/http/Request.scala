package urwerk.io.http

import urwerk.io
import urwerk.io.http.Method
import urwerk.io.Uri

case class Request(uri: Uri, method: Method)

object Request:
  object Get:
    def apply(uri: String): Request =
      apply(Uri(uri))
  
    def apply(uri: Uri): Request =
      Request(uri, Method.Get)