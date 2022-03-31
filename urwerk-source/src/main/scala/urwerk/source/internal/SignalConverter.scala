package urwerk.source.internal

import reactor.core.publisher.Signal as UnderlyingSignal
import reactor.util.context.Context as UnderlyingContext
import urwerk.source.{Context, Signal}
import urwerk.source.Signal.{Complete, Error, Next, Subscribe}

private object SignalConverter:
  def fromUnderlying[A](signal: UnderlyingSignal[A]): Signal[A] =
    val context = ContextAdapter.wrap(signal.getContextView)

    if signal.isOnComplete then
      Complete()(context)
    else if signal.isOnError then
      Error(signal.getThrowable)(context)
    else if signal.isOnNext then
      Next(signal.get)(context)
    else if signal.isOnSubscribe then
      Subscribe(SubscriptionConverter.fromUnderlying(signal.getSubscription))(context)
    else
      ???

  def toUnderlying[A](signal: Signal[A]): UnderlyingSignal[A] =
     val context = UnderlyingContext.of(
       ContextConverter.toUnderlying(signal.context))

     signal match
       case Complete() =>
         UnderlyingSignal.complete(context)
       case Next(value) =>
         UnderlyingSignal.next(value, context)
       case Error(error) =>
         UnderlyingSignal.error(error, context)
       case Subscribe(subscription) =>
         val underlyingSubscription = SubscriptionConverter.toUnderlying(subscription)
         UnderlyingSignal.subscribe(underlyingSubscription, context)
