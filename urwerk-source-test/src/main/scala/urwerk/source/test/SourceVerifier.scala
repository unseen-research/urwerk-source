package urwerk.source.test

import _root_.reactor.test.StepVerifier
import _root_.reactor.test.StepVerifier.FirstStep

import urwerk.source.Optional
import urwerk.source.Singleton
import urwerk.source.Source

import urwerk.source.reactor.FluxConverters.*

object SourceVerifier:
  def apply[A](source: Source[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: Source[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)
  
object SingletonVerifier:

  def apply[A](source: Singleton[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: Singleton[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)

object OptionalVerifier:

  def apply[A](source: Optional[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: Optional[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)

extension[A](source: Source[A])
  def toVerifier  = SourceVerifier(source)

extension[A](source: Singleton[A])
  def assertSingleton = source
  def toVerifier  = SingletonVerifier(source)

extension[A](source: Optional[A])
  def assertOptional = source
  def toVerifier  = OptionalVerifier(source)
