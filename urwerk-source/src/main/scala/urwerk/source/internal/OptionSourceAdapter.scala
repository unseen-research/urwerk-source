package urwerk.source.internal

import reactor.core.publisher.Flux
import urwerk.source.{Context, OptionSource, OptionalFactory}

import scala.jdk.OptionConverters.*

private[source] object OptionSourceAdapter extends OptionalFactory:
  def apply[A](elem: A): OptionSource[A] =
    wrap(Flux.just(elem))

  def apply[A](): OptionSource[A] =
    wrap(Flux.empty())

  def apply[A](elemOpt: Option[A]): OptionSource[A] =
    elemOpt match
      case Some(elem) => apply(elem)
      case None => apply()

  def empty[A]: OptionSource[A] =
    wrap(Flux.empty())

  def error[A](error: Throwable): OptionSource[A] =
    wrap(Flux.error(error))

  private[internal] def wrap[B](flux: Flux[B]): OptionSource[B] =
    new OptionSourceAdapter(flux)

private class OptionSourceAdapter[+A](flux: Flux[_<: A]) extends SourceOpsAdapter[A](flux), OptionSource[A]:
  type S[A] = OptionSource[A]

  protected def wrap[B](flux: Flux[? <: B]): OptionSource[B] = OptionSourceAdapter.wrap(flux)

  def block: Option[A] =
    stripReactiveException(
      flux.next.blockOptional.toScala)

  override def filter(pred: A => Boolean): OptionSource[A] =
    OptionSourceAdapter.wrap(flux.filter(pred(_)))

  override def filterNot(pred: A => Boolean): OptionSource[A] =
    filter(!pred(_))
