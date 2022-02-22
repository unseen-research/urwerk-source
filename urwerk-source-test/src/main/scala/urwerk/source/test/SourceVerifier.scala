package urwerk.source.test

import _root_.reactor.test.StepVerifier
import _root_.reactor.test.StepVerifier.FirstStep

import urwerk.source.OptionSource
import urwerk.source.SingletonSource
import urwerk.source.Source

import urwerk.source.reactor.FluxConverters.*

object SourceVerifier:
  def apply[A](source: Source[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: Source[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)
  
object SingletonSourceVerifier:

  def apply[A](source: SingletonSource[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: SingletonSource[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)

object OptionSourceVerifier:

  def apply[A](source: OptionSource[A], request: Long): FirstStep[A] = 
    StepVerifier.create(source.toFlux, request)

  def apply[A](source: OptionSource[A]): FirstStep[A] = 
    apply(source, Long.MaxValue)

extension[A](source: Source[A])
  def toVerifier  = SourceVerifier(source)

extension[A](source: SingletonSource[A])
  def assertSingleton = source
  def toVerifier  = SingletonSourceVerifier(source)

extension[A](source: OptionSource[A])
  def assertOptional = source
  def toVerifier  = OptionSourceVerifier(source)
