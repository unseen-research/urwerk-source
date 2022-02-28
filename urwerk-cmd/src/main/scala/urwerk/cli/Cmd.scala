package urwerk.cli

import scala.deriving.Mirror

class Param

object Cmd:
  inline def apply[A <: Product](using m: Mirror.ProductOf[A]) : Cmd[A] = 
    new Cmd()

  sealed trait Setting

  class Bind: 
    def / (name: String): BindingKey = new BindingKey(name)

  val bind = Bind()

  class BindingKey(name: String):
    def :=[A] (value: A): Binding[A] = Binding(name, value)

    def := (scope: BindingScope): BindingScope = ???

  class BindingScope(scope: String):
    def / (param: Param): Binding[?] = ???

  class Binding[A](name: String, value: A) extends Setting
  
  extension [C <: Product](cmd: Cmd[C])
    def execute(args: String*): Int = 
      ???

end Cmd

class Cmd[A <: Product]():
  import Cmd.*

  def apply(setting: Setting, settings: Setting*): Cmd[A] = 
    ???

  
