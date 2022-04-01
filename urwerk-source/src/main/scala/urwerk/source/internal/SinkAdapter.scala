package urwerk.source.internal

import reactor.core.publisher.FluxSink as UnderlyingSink
import urwerk.source.Sink

private class SinkAdapter[A](sink: UnderlyingSink[A]) extends Sink[A]:
  def complete(): Unit = sink.complete()

  def error(error: Throwable): Unit = sink.error(error)

  def isCancelled: Boolean = sink.isCancelled

  def next(elem: A): Sink[A] = SinkAdapter(sink.next(elem))

  def onCancel(op: => Unit): Sink[A] = SinkAdapter(sink.onCancel(() => op))

  def onDispose(op: => Unit): Sink[A] = SinkAdapter(sink.onDispose(() => op))

  def onRequest(op: Long => Unit): Sink[A] = SinkAdapter(sink.onRequest(op(_)))

  def requested: Long =
    sink.requestedFromDownstream()
