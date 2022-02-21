package urwerk.source.internal

import reactor.core.publisher.Flux
import urwerk.source.Optional

import scala.jdk.OptionConverters.*
import urwerk.source.OptionalFactory

private[source] object FluxOptional extends OptionalFactory:
  def apply[A](elem: A): Optional[A] =
    wrap(Flux.just(elem))

  def apply[A](): Optional[A] =
    wrap(Flux.empty())

  def apply[A](elemOpt: Option[A]): Optional[A] =
    elemOpt match
      case Some(elem) => apply(elem)
      case None => apply()

  def empty[A]: Optional[A] =
    wrap(Flux.empty())

  def error[A](error: Throwable): Optional[A] =
    wrap(Flux.error(error))

  private[internal] def wrap[B](flux: Flux[B]): Optional[B] =
    new FluxOptional(flux)

private class FluxOptional[+A](flux: Flux[_<: A]) extends FluxSourceOps[A](flux), Optional[A]:

  type S[A] = Optional[A]

  protected def wrap[B](flux: Flux[? <: B]): Optional[B] = FluxOptional.wrap(flux)

  def block: Option[A] =
    stripReactiveException(
      flux.next.blockOptional.toScala)

  override def filter(pred: A => Boolean): Optional[A] =
    FluxOptional.wrap(flux.filter(pred(_)))

  override def filterNot(pred: A => Boolean): Optional[A] =
    filter(!pred(_))
