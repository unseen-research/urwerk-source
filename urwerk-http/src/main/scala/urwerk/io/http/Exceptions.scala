package urwerk.io.http

class HttpException(message: String) extends RuntimeException(message: String):
  def withCause(cause: Throwable): Throwable =
    initCause(cause)

class HttpStatusException(val statusCode: Int, message: String) extends HttpException(message):
  def this(statusCode: Int) = this(statusCode, s"Http request failed: statusCode=$statusCode")
