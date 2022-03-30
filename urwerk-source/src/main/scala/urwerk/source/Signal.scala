package urwerk.source

import java.util.concurrent.Flow.Subscription
import urwerk.source.Context

object Signal:

  object Complete:
    def apply(): Complete = Complete()(Context.empty)

  final case class Complete()(val context: Context) extends Signal[Nothing]

  object Error:
    def apply(error: Throwable): Error = Error(error)(Context.empty)
    
  final case class Error(error: Throwable)(val context: Context = Context.empty) extends Signal[Nothing]

  object Next:
    def apply[A](value: A): Next[A] = Next(value)(Context.empty)
  
  final case class Next[+A](value: A)(val context: Context = Context.empty) extends Signal[A]

  object Subscribe:
    def apply(subscription: Subscription): Subscribe = Subscribe(subscription)(Context.empty)
    
  final case class Subscribe(subscription: Subscription)(val context: Context = Context.empty) extends Signal[Nothing]

sealed trait Signal[+A]:
  val context: Context


