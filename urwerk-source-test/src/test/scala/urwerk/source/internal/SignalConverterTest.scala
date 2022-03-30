package urwerk.source.internal

import reactor.core.publisher.Signal as UnderlyingSignal
import reactor.util.context.Context as UnderlyingContext
import urwerk.source.{Context, Signal}
import urwerk.test.TestBase

class SignalConverterTest extends TestBase:
  "convert complete signal" in {
    val underlying = UnderlyingSignal.complete(UnderlyingContext.of("abc", "ABC"))

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be(Signal.Complete())
    signal.context should be (Context("abc" -> "ABC"))
  }

  "convert next signal" in {
    val underlying = UnderlyingSignal.next(77, UnderlyingContext.of("abc", "ABC"))

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be(Signal.Next(77))
    signal.context should be (Context("abc" -> "ABC"))
  }

  "convert error signal" in {
    val error = IllegalArgumentException("msg")
    val underlying = UnderlyingSignal.error(error, UnderlyingContext.of("abc", "ABC"))

    val signal = SignalConverter.fromUnderlying(underlying)

    signal should be (Signal.Error(error))
  }