package urwerk.cmdln

object Parameters:
  def param[A](name: String, names: String*): Parameters[A] = ???

  def param[A]: Parameters[A] = ???

  def section: Parameters[?] = ???

  def section(name: String): Parameters[?] = ???



class Parameters[+A]:
  val x = ""

  def flatMap[B](fn: A => Parameters[B]): Parameters[B] = ???

  def map[B](fn: A => B): Parameters[B] = ???

