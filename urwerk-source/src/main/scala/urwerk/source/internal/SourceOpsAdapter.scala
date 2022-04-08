package urwerk.source.internal

import org.reactivestreams.{FlowAdapters, Publisher}
import reactor.adapter.JdkFlowAdapter
import reactor.core.publisher.{Flux, FluxCreate, Mono}
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context as UnderlyingContext
import urwerk.source.*
import urwerk.source.internal.{ContextAdapter, given}
import urwerk.source.reactor.FluxConverters
import urwerk.source.reactor.FluxConverters.*

import java.util.concurrent.Flow
import java.util.function.{BiConsumer, BiFunction}
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.*
import scala.jdk.FunctionConverters.*


private abstract class SourceOpsAdapter[+A](val flux: Flux[_ <: A]):
  type  S[+ _]

  protected def wrap[B](flux: Flux[? <: B]): S[B]

  def cache: S[A] = wrap(flux.cache())

  def concat[B](implicit evidence: Source[A] <:< Source[Source[B]]): Source[B] =
    SourceAdapter.wrap(
      Flux.concat(
        flux.asInstanceOf[Flux[Source[B]]]
          .map(_.toFlux)))

  def concatDelayError[B](implicit evidence: Source[A] <:< Source[Source[B]]): Source[B] =
    SourceAdapter.wrap(
      Flux.concatDelayError(
        flux.asInstanceOf[Flux[Source[B]]]
          .map(_.toFlux)))

  def concat[A1 >: A](other: Source[A1]): Source[A1] =
    SourceAdapter.wrap(
      flux.asInstanceOf[Flux[A1]]
        .concatWith(other.toFlux))

  def dematerialize[B](implicit evidence: Source[A] <:< Source[Signal[B]]): S[B] =
    wrap(flux
      .map(signal =>
        signal.asInstanceOf[Signal[A]])
      .map(signal => SignalConverter.toUnderlying(signal))
      .dematerialize())

  def distinct: S[A] = wrap(flux.distinct)

  def doFinally(op: () => Unit): S[A] =
    wrap(flux.doFinally(_ => op()))

  def doOnComplete(op: () => Unit): S[A] =
    wrap(flux.doOnComplete(() => op()))

  def doOnEach(op: Signal[A] => Unit): S[A] =
    wrap(
      flux.doOnEach(signal =>
        op(SignalConverter.fromUnderlying(signal))))

  def doOnError(op: Throwable => Unit): S[A] =
    wrap(
      flux.doOnError(op(_)))

  def doOnNext(op: A => Unit): S[A] =
    wrap(
      flux.doOnNext(op(_)))

  def flatMap[B](op: A => Source[B]): Source[B] =
    SourceAdapter.wrap(
      flux.flatMap(
        op(_).toFlux))

  def flatMap[B](op: A => S[B]): S[B] =
    wrap(
      flux.flatMap(elem =>
        unwrap(op(elem).asInstanceOf[Source[B]])))

  def flatMap[B](concurrency: Int)(op: A => Source[B]): Source[B] =
    SourceAdapter.wrap(
      flux.flatMap(op(_).toFlux,
      concurrency))

  def flatMap[B](concurrency: Int, prefetch: Int)(op: A => Source[B]): Source[B] =
    SourceAdapter.wrap(
      flux.flatMap(op(_).toFlux,
      concurrency,
      prefetch))

  def foldLeft[B](start: B)(op: (B, A) => B): urwerk.source.SingletonSource[B] =
    SingletonSourceAdapter.wrap(
      flux.reduce(start,
        op(_, _)).flux)

  def head: urwerk.source.SingletonSource[A] =
    SingletonSourceAdapter.wrap(
      flux
        .next()
        .single().flux())

  def headOption: OptionSource[A] =
    OptionSourceAdapter.wrap(
      flux
        .next().flux())

  def last: SingletonSource[A] =
    SingletonSourceAdapter.wrap(
      flux
        .last().flux())

  def lastOption: OptionSource[A] =
    OptionSourceAdapter.wrap(
      flux
        .last()
        .onErrorResume(
          classOf[NoSuchElementException],
          _ => Mono.empty)
        .flux())

  def map[B](op: A => B): S[B] =
    wrap(flux.map(op(_)))

  def materialize: S[Signal[A]] =
    wrap(
      flux.materialize.map(signal => SignalConverter.fromUnderlying(signal)))

  def merge[A1 >: A](that: Source[A1]): Source[A1] =
    SourceAdapter.wrap((
      Flux.merge(flux, unwrap(that))))

  def merge[B](implicit evidence: Source[A] <:< Source[Source[B]]): Source[B] =
    SourceAdapter.wrap(
      Flux.merge(
        flux.asInstanceOf[Flux[Source[B]]]
          .map(_.toFlux)))

  def mergeDelayError[A1 >: A](prefetch: Int, that: Source[A1]): Source[A1] =
    SourceAdapter.wrap(
      Flux.mergeDelayError(prefetch, flux, unwrap(that)))

  def mkString(start: String, sep: String, end: String): SingletonSource[String] =
    foldLeft(StringBuilder(start))((builder, elem) =>
        builder.append(elem.toString)
          .append(sep))
      .map(_.dropRight(sep.size)
        .append(end)
        .toString)

  def onBackpressureBuffer(capacity: Int, overflowStrategy: BufferOverflowStrategy): S[A] =
    wrap(
      flux.onBackpressureBuffer(capacity, overflowStrategy.asJava))

  def onErrorContinue(op: (Throwable, Any) => Unit): Source[A] =
    SourceAdapter.wrap(
      flux.onErrorContinue(op.asJava))

  def onErrorMap(op: Throwable => Throwable): S[A] =
    wrap(
      flux.onErrorMap(op.asJava))

  def onErrorResume[A1 >: A](op: Throwable => Source[A1]): Source[A1] =
    SourceAdapter.wrap(
      flux.asInstanceOf[Flux[A1]]
        .onErrorResume{(e) => op(e).toFlux})

  def onErrorResume[A1 >: A](op: Throwable => S[A1]): S[A1] =
    wrap(
      flux.asInstanceOf[Flux[A1]]
              .onErrorResume{(e) => unwrap(op(e).asInstanceOf[Source[A1]])})

  def publishOn(ec: ExecutionContext): S[A] =
    wrap(
      flux.publishOn(Schedulers.fromExecutor(ec.toExecutor)))

  def reduce[A1 >: A](op: (A1, A) => A1): OptionSource[A1] =
    def reduceOp[B1 <: A]: BiFunction[B1, B1, B1] = (v1, v2) =>
      op(v1, v2).asInstanceOf[B1]

    OptionSourceAdapter.wrap(flux.reduce(reduceOp).flux)

  def scan[B](start: B)(op: (B, A) => B): S[B] =
    wrap(
      flux.scan(start, op.asJavaBiFunction))

  def scanWith[B](start: => B)(op: (B, A) => B): S[B] =
    wrap(
      flux.scanWith(()=> start, op.asJavaBiFunction))

  def subscribe(): AutoCloseable = {
    val disposable = flux.subscribe()
    () => {
      disposable.dispose()
    }
  }

  def subscribe[A1 >: A](subscriber: Flow.Subscriber[A1]): Unit = {
    flux.subscribe(
      FlowAdapters.toSubscriber(subscriber))
  }

  def subscribe(onNext: A => Unit, onError: Throwable => Unit, onComplete: => Unit ): AutoCloseable = {
    val disposable = flux.subscribe(onNext(_), onError(_), () => onComplete)
    () => {
      disposable.dispose()
    }
  }

  def subscribeOn(ec: ExecutionContext): S[A] =
    wrap(
      flux.subscribeOn(Schedulers.fromExecutor(ec.toExecutor)))

  def subscribeOn(ec: ExecutionContext, requestOnSeparateThread: Boolean): S[A] =
    wrap(
      flux.subscribeOn(Schedulers.fromExecutor(ec.toExecutor), requestOnSeparateThread))


  def takeUntil(predicate: A => Boolean): S[A] =
    wrap(
      flux.takeUntil(predicate.asJava))

  def takeWhile(predicate: A => Boolean): S[A] =
    wrap(
      flux.takeWhile(predicate.asJava))

  def toPublisher[A1 >: A]: Flow.Publisher[A1] =
    JdkFlowAdapter.publisherToFlowPublisher(flux.asInstanceOf[Flux[A1]])

  def toSeq: SingletonSource[Seq[A]] =
    SingletonSourceAdapter.wrap(flux
      .collectList
      .flux.map(_.asScala.toSeq))

  def updatedContext(context: Context): S[A] =
    wrap(
      flux.contextWrite(
        ContextConverter.toUnderlying(context)))

  def updatedContextWith(map: Context => Context): S[A] =
    wrap(flux
      .contextWrite{ context =>
        val newContext = map(ContextAdapter.wrap(context))
        val underlyingContextView = ContextConverter.toUnderlying(newContext)
        UnderlyingContext.of(underlyingContextView)
      })
