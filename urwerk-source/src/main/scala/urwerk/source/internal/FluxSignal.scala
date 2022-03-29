package urwerk.source.internal

import reactor.core.publisher.Signal as UnderlyingSignal
import urwerk.source.Signal
import urwerk.source.Signal.{Next, Complete, Error}

private object FluxSignal:
  def wrap[A](signal: UnderlyingSignal[A]): Signal[A] =
    if signal.isOnError() then
        Error(signal.getThrowable())
    else if signal.isOnNext() then
        Next(signal.get)
    else if signal.isOnComplete() then
        Complete
    else throw UnsupportedOperationException()


class FluxSignal[+A](underlying: UnderlyingSignal[A])




