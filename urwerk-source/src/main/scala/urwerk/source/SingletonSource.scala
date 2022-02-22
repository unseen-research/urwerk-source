package urwerk.source

import java.util.concurrent.CompletableFuture
import urwerk.source.internal.FluxSingleton
import scala.concurrent.Future
import scala.util.Try

object SingletonSource extends SingletonFactory:
  export FluxSingleton.*

trait SingletonSource[+A] extends Source[A]:
  def block: A

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