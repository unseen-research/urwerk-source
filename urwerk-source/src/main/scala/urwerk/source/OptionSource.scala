package urwerk.source

import urwerk.source.internal.OptionSourceAdapter

object OptionSource extends OptionalFactory:
  export OptionSourceAdapter.*

trait OptionSource[+A] extends Source[A]:
  def block: Option[A]

  def doFinally(op: () => Unit): OptionSource[A]

  def doOnComplete(op: () => Unit): OptionSource[A]

  def doOnEach(op: Signal[A] => Unit): OptionSource[A]

  def doOnError(op: Throwable => Unit): OptionSource[A]

  def doOnNext(op: A => Unit): OptionSource[A]
  
  def filter(pred: A => Boolean): OptionSource[A]

  def filterNot(pred: A => Boolean): OptionSource[A]

  def flatMap[B](op: A => OptionSource[B]): OptionSource[B]

  def map[B](op: A => B): OptionSource[B]

trait OptionalFactory:
  def apply[A](elem: A): OptionSource[A]

  def apply[A](): OptionSource[A]

  def apply[A](elemOpt: Option[A]): OptionSource[A]

  def empty[A]: OptionSource[A]

  def error[A](error: Throwable): OptionSource[A]
