package urwerk.source

import java.util.concurrent.CompletableFuture
import urwerk.source.internal.SingletonSourceAdapter
import scala.concurrent.Future
import scala.util.Try

object SingletonSource extends SingletonFactory:
  export SingletonSourceAdapter.*

trait SingletonSource[+A] extends Source[A]:
  def block: A

  def doFinally(op: () => Unit): SingletonSource[A]

  def doOnComplete(op: () => Unit): SingletonSource[A]

  def doOnEach(op: Signal[A] => Unit): SingletonSource[A]

  def doOnError(op: Throwable => Unit): SingletonSource[A]

  def doOnNext(op: A => Unit): SingletonSource[A]

  def filter(pred: A => Boolean): OptionSource[A]

  def filterNot(pred: A => Boolean): OptionSource[A]

  def flatMap[B](op: A => SingletonSource[B]): SingletonSource[B]

  def map[B](op: A => B): SingletonSource[B]

trait SingletonFactory:
  def apply[A](elem: A): SingletonSource[A]

  def defer[A](op: => SingletonSource[A]): SingletonSource[A]

  def error[A](error: Throwable): SingletonSource[A]

  def from[A](future: CompletableFuture[A]): SingletonSource[A]

  def from[A](future: Future[A]): SingletonSource[A]

  def from[A](elemTry: Try[A]): SingletonSource[A]