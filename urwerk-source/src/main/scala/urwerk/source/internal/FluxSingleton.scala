package urwerk.source.internal

import java.util.concurrent.CompletableFuture

import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

import scala.concurrent.Future
import scala.jdk.FutureConverters.given
import scala.util.Try
import scala.util.Success
import scala.util.Failure

import urwerk.source.Source
import urwerk.source.OptionSource
import urwerk.source.reactor.FluxConverters.*
import urwerk.source.SingletonSource
import urwerk.source.SingletonFactory
import urwerk.source.SourceException
import scala.collection.View.Single
import urwerk.source.Context

private[source] object FluxSingleton extends SingletonFactory:

  def apply[A](elem: A): SingletonSource[A] =
    wrap(Flux.just(elem))

  def defer[A](op: => SingletonSource[A]): SingletonSource[A] =
    wrap(Flux.defer(() =>
      op.toFlux))

  def error[A](error: Throwable): SingletonSource[A] =
    wrap(Flux.error(error))

  def from[A](future: CompletableFuture[A]): SingletonSource[A] =
    wrap(
      Mono.fromFuture(future)
        .flux())

  def from[A](future: Future[A]): SingletonSource[A] =
    from(
      future.asJava.toCompletableFuture)

  def from[A](elemTry: Try[A]): SingletonSource[A] = elemTry match
    case Success(elem) =>
      SingletonSource(elem)
    case Failure(e) =>
      SingletonSource.error(e)

  private[internal] def wrap[A](flux: Flux[A]): SingletonSource[A] =
    val fluxSingleton = new FluxSingleton(flux)
    new SingletonSource[A]{
      export fluxSingleton.*
    }

private class FluxSingleton[+A](flux: Flux[? <: A]) extends FluxSourceOps[A](flux), SingletonSource[A]:
  type S[A] = SingletonSource[A]

  protected def wrap[B](flux: Flux[? <: B]): SingletonSource[B] = FluxSingleton.wrap(flux)

  def block: A =
    stripReactiveException(flux.blockFirst())

  def filter(pred: A => Boolean): OptionSource[A] =
    FluxOptional.wrap(flux.filter(pred(_)))

  def filterNot(pred: A => Boolean): OptionSource[A] =
    filter(!pred(_))
    