package urwerk.hkd

import urwerk.test.TestBase

import scala.deriving.*

def objectToTuple[A <: Product](value: A)(using mirror: Mirror.ProductOf[A]): mirror.MirroredElemTypes =
  Tuple.fromProductTyped(value)

def tupleToObject[A](value: Tuple)(using mirror: Mirror.ProductOf[A], ev: value.type <:< mirror.MirroredElemTypes): A = mirror.fromProduct(value)

case class Vehicle(brand: String, wheels: Int)

trait Param[A]:
  def get(a: A): String

extension [A <: Tuple] (tuple: A)
  def to[B <: Product](using mirror: Mirror.ProductOf[B], ev: tuple.type <:< mirror.MirroredElemTypes): B = mirror.fromProduct(tuple)

//case class OT [A <: Tuple: Params]()
//  //type ET = Tuple.InverseMap[A, Param]
//
//  def to[B <: Product](using mirror: Mirror.ProductOf[B], ev: tuple.type <:< mirror.MirroredElemTypes): B =
//
//    mirror.fromProduct(tuple)

trait Params[A]:
  def handleParam(a: A): String

object OT:
  given Param[String] with
    def get(a: String): String = "Right()"

  given Param[Int] with
    def get(a: Int): String = "Right(77)"

  given Params[EmptyTuple] with
    def handleParam(a: EmptyTuple): String = ""

  given[H : Param, T <: Tuple : Params]: Params[H *: T] with
    def handleParam(tuple: H *: T): String =
      summon[Param[H]].get(tuple.head) + summon[Params[T]].handleParam(tuple.tail)

    //def encodeRow(tuple: H *: T) = summon[FieldEncoder[H]].encodeField(tuple.head) :: summon[RowEncoder[T]].encodeRow(tuple.tail)

//extension [X <: Tuple ](tuple: X)(using Params[X])
extension [X <: Tuple: Params](tuple: X)
  def toObject[B <: Product]: B =
    val zz = summon[Params[X]].handleParam(tuple)
    ???

//recursive tupling??
//https://stackoverflow.com/questions/69129866/scala-3-tuple-from-nested-case-class
def flatTuple[A <: Tuple](tuple: A): Tuple = {
  tuple match {
  case x *: xs =>
    x match {
    case x: Product =>
      val tuple2 = Tuple.fromProduct(x) // ***
      flatTuple(tuple2) ++ flatTuple(xs)
    case x => x *: flatTuple(xs)
    }
  case _ => EmptyTuple
  }
}

import OT.given

case class Abc(a: String):
  def print() = ""

class TupleTest extends TestBase:
  "dd" in {
    val xx = ("", 77).toObject[Vehicle]
    //val v: Vehicle =
      //Params((Param[String]()))
    //.to[Vehicle]
  }
  "object to tuple" in {
    objectToTuple(Vehicle(brand = "Lada", wheels = 4)) should be ("Lada", 4)
  }

  "tuple to object" in {
    tupleToObject[Vehicle]("Simson", 2) should be (Vehicle("Simson", 2))
  }

  "to object extension" in {
    val vehicle: Vehicle = ("Simson", 2).to[Vehicle]
    vehicle should be (Vehicle("Simson", 2))
  }


