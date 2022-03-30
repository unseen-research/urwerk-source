package urwerk.source.internal

import reactor.core.publisher.Signal as UnderlyingSignal
import urwerk.source.Context
import urwerk.source.Signal
import urwerk.source.Signal.{Complete, Error, Next, Subscribe}

private object SignalConverter:
  def fromUnderlying[A](signal: UnderlyingSignal[A]): Signal[A] =
    val context = FluxContext.wrap(signal.getContextView)

    if signal.isOnComplete then
      Complete()(context)
    else if signal.isOnError then
      Error(signal.getThrowable)(context)
    else if signal.isOnNext then
      Next(signal.get)(context)
    else if signal.isOnSubscribe then
      ???//Subscribe(signal.getSubscription)(contextProvider)
    else
      ???

