package urwerk.source.internal

import reactor.core.publisher.Signal as UnderlyingSignal
import reactor.util.context.Context as UnderlyingContext
import org.reactivestreams.Subscription as UnderlyingSubscription
import urwerk.source.internal.SubscriptionConverter.AsSubscription

import java.util.concurrent.Flow
import urwerk.source.{Context, Signal}
import urwerk.test.TestBase

class SignalConverterTest extends TestBase:
  "convert underlying signal to complete" in {
    val underlying = UnderlyingSignal.complete(underlyingContext)

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be(Signal.Complete())
    signal.context should be (context)
  }

  "convert underlying signal to  next" in {
    val underlying = UnderlyingSignal.next(77, underlyingContext)

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be(Signal.Next(77))
    signal.context should be (context)
  }

  "convert underlying signal to  error" in {
    val underlying = UnderlyingSignal.error(error, underlyingContext)

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be (Signal.Error(error))
  }

  "convert underlying signal to subscribe" in {
    val underlying = UnderlyingSignal.subscribe(underlyingSubscription, underlyingContext)

    val signal@Signal.Subscribe(subscription) = SignalConverter.fromUnderlying(underlying)

    signal.context should be (context)
    val actualUnderlyingSubscripton = signal.subscription.asInstanceOf[AsSubscription].subscription
    actualUnderlyingSubscripton should be theSameInstanceAs underlyingSubscription
  }

  "convert complete to underlying" in {
    val signal = SignalConverter.toUnderlying(Signal.Complete()(context))

    signal should be (UnderlyingSignal.complete(underlyingContext))
  }

  "convert next to underlying" in {
    val signal = SignalConverter.toUnderlying(Signal.Next(77)(context))

    signal should be (UnderlyingSignal.next(77, underlyingContext))
  }

  "convert error to underlying" in {
    val signal = SignalConverter.toUnderlying(Signal.Error(error)(context))

    signal should be (UnderlyingSignal.error(error, underlyingContext))
  }

  "convert subscribe to underlying" in {
    val signal = SignalConverter.toUnderlying(Signal.Subscribe(subscription)(context))

    val underlyingSignal = UnderlyingSignal.subscribe(underlyingSubscription, underlyingContext)

    underlyingSignal.isOnSubscribe should be (true)
    underlyingSignal.getContextView should be (underlyingContext)
    Option(underlyingSignal.getSubscription).isDefined should be (true)
  }

  val context = Context("abc" -> "ABC")

  val underlyingContext = UnderlyingContext.of("abc", "ABC")

  val subscription = new Flow.Subscription:
    def request(n: Long) = ()
    def cancel() = ()

  val underlyingSubscription = new UnderlyingSubscription:
    def request(n: Long) = ()
    def cancel() = ()

  val error = IllegalArgumentException("msg")