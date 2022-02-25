package urwerk.cli

import urwerk.test.TestBase

import scala.deriving.Mirror
import scala.compiletime.summonAll
import scala.compiletime.erasedValue
import scala.compiletime.summonInline
import scala.compiletime.summonFrom
import scala.compiletime.{constValue, constValueTuple}
import scala.collection.mutable.ArrayBuffer


class DeclineTest extends TestBase:

  case class Employee(name: String, number: Int, manager: Boolean)
  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  trait FieldEncoder[A]:
    def encodeField(a: A): String

  type Row = List[String]

  trait RowEncoder[A]:
    def encodeRow(a: A): Row


  object BaseEncoders:
    given FieldEncoder[Int] with
      def encodeField(x: Int) = x.toString

    given FieldEncoder[Boolean] with
      def encodeField(x: Boolean) = if x then "true" else "false"

    given FieldEncoder[String] with
      def encodeField(x: String) = x // Ideally, we should also escape commas and double quotes
  end BaseEncoders    


  object TupleEncoders:
    // Base case
    given RowEncoder[EmptyTuple] with
      def encodeRow(empty: EmptyTuple) =
        List.empty

    // Inductive case
    given [H: FieldEncoder, T <: Tuple: RowEncoder]: RowEncoder[H *: T] with
      def encodeRow(tuple: H *: T) =
        summon[FieldEncoder[H]].encodeField(tuple.head) :: summon[RowEncoder[T]].encodeRow(tuple.tail)
  end TupleEncoders


  def tupleToCsv[X <: Tuple : RowEncoder](tuple: X): List[String] =
    summon[RowEncoder[X]].encodeRow(tuple)


  "main" in {
    val bob: Employee = Employee("Bob", 42, false)
    val bobTuple: (String, Int, Boolean) = Tuple.fromProductTyped(bob)
    println(s"TUPLE $bobTuple")
    val bm = summon[Mirror.Of[Employee]]
    
    val bobAgain: Employee = bm.fromProduct(("other bob", 42, false))
    println(s"BobAGAIN $bobAgain")

  }
  
  "elem labels" in {
    val mirror = summon[Mirror.Of[Employee]]
        
    type ValueOfs = Tuple.Map[mirror.MirroredElemLabels, ValueOf]

    val valueOfs = summonAll[ValueOfs]

    def values(t: Tuple): Tuple = t match
      case (h: ValueOf[_]) *: t1 => h.value *: values(t1)
      case EmptyTuple => EmptyTuple

    val x = values(valueOfs) // (i,s)

    println(s"Lables $x")
  }

  "elem types" in {

    case class Data(x: String, y: List[Boolean])
    val mirror = summon[Mirror.Of[Data]]

    val xx = describe[mirror.MirroredElemTypes]

    println(s"TYPES $xx")

    val data: Data = summon[Mirror.Of[Data]].fromProduct(("string value", Seq("truexxx", false)))

    println(s"DAT $data")
    
    val listBool: List[Boolean] = data.y

    //listBool.map(!_)

    println(s"BOOLS $listBool")
  }

  trait A
  given A with {
    override def toString="an A"
  }

  trait B
  given B with {
    override def toString="a  B"
  }

  trait C
  given C with {
    override def toString="a  C"
  }

  "summon all" in {
    val x = summonAll[(A, B, C)]

    x.toList.foreach{elem=>
      println(s"Elem $elem")   
    }

    // Command[Config](
    //   //"a".help("")

    // )


  }

  "singleton value of" in {
    val sv = "abcx"

    val value = valueOf[sv.type]
    println(s"VALUEOf $value")

  }

  "svo 2" in {
    def xxx[A](using m: ValueOf[A]) = 
      println(s"VALUEOF2 ${m.value}")
    xxx[4]

    import  scala.compiletime.summonInline
    import  scala.compiletime.erasedValue

    inline def yyy[A] = 

      // inline erasedValue[A] match 
      //   case _: Singleton =>
      //     val value = summonInline[ValueOf[A]].value
      //     println(s"YYY ${value}")
      //   case _ => println("otherwise")

      val value = summonInline[ValueOf[A]].value
      println(s"VALUEOF222 ${value}")

    yyy[4]
//    yyy[String]
    class X :
      val a=""

      def -> (): Unit = ()

    X()->()
  }

  "xxx xxx" in {

    class Y:
      def fn[A](value: A): String = 
        println(s"VALUE: $value")
        ""

      def fn[A <: Singleton](value: A)(using ValueOf[A]): String = 
        val _type = valueOf[A]
        println(s"SingletonVALUE: $value ${_type}")
        ""



    Y().fn[String]("abc")

    Y().fn["xyz"]("xyz")
    
  }

  "svo 3" in {
    def operate[A <: Singleton](v: A): A = 
      //summon[ValueOf[A]]
      v
    val it = operate(3)


    val xx = summon[ValueOf[77]].value
    println(s"XXXX $xx")
  }

  "singleton type " in {
    case class Wrapper[A <: String](a: A)(using ValueOf[A])

    val w = Wrapper["String"]("String")
  }
  
  // trait Value[A]:
  //   def get: A

  // case class StringValue(get: String) extends Value[String]
  
  // case class IntValue(get: Int) extends Value[Int]

  trait ValueHandler[A]:
    def valueTypeLabel: String

  given ValueHandler[String] with
    def valueTypeLabel: String = "Stringtype"
  
  given ValueHandler[Int] with
    def valueTypeLabel: String = "Inttype"
  
    //given listOrd[T](using ord: Ord[T]): Ord[List[T]] with
  given valhand[A](using x: ValueHandler[A]): ValueHandler[Seq[A]] with
    def valueTypeLabel: String = s"Seq[${x.valueTypeLabel}+++]"

  
  case class Binding[A](name: String, value: A)

  class BindingKey(name: String):
    def / (name: String): BindingKey = BindingKey(name)

    def := [A] (value: A): Binding[A] = Binding(name, value)

  val bind = BindingKey("")

  object BindingProbe: 
    inline def apply[A](using m: Mirror.ProductOf[A]) : BindingProbe[A] = 
      val name = constValue[m.MirroredLabel]
      val names = constValueTuple[m.MirroredElemLabels].productIterator.map(_.toString)

      type TypeHandler = Tuple.Map[m.MirroredElemTypes, ValueHandler]
      val elemTransformers = summonAll[TypeHandler].toList.asInstanceOf[List[ValueHandler[?]]]  

      elemTransformers.foreach{vh=>
        println(s"VALUE HANDler ${vh.valueTypeLabel}")
      }
      println(s"ClassName=$name: props=$names")
     
      def valueTuple(properties: Map[String, Any]): Tuple = 
        val values = names.map{name=> properties(name)}.toArray
        Tuple.fromArray(values)

      def fromTuple(tuple: Tuple): A = 
        m.fromProduct(tuple)
      
      new BindingProbe(Seq(), valueTuple, fromTuple)

  class BindingProbe[A](bindings: Seq[Binding[?]], propertiesOp: Map[String, Any] => Tuple, fromTupleOp: Tuple=> A):
    def apply(bindings: Binding[?]*): BindingProbe[A] = 
      new BindingProbe(bindings, propertiesOp, fromTupleOp)
    
    def toConfig: A = 
      val propMap = bindings.foldLeft(Map[String, Any]()){case (properties, Binding(name, value)) =>
        properties.updated(name, value)
      }
      val values = propertiesOp(propMap)

      fromTupleOp(values)

  "bind test" in {
    case class Config(abc: String, xyz: Int, seq: Seq[String] = Seq())  
    val bp = BindingProbe[Config](
      bind / "abc" := "value", 
      bind / "xyz" := 77,
      bind / "seq" := Seq("a", "b"))

    bp.toConfig should be (Config("value", 77, Seq("a", "b")))
  }

  "test otehr" in {
    val / = "slash value"
    println(/) 
  }

  
// trait FieldCollector[T] {
//   def collect(fields: Collector): Collector
// }
  
// object FieldCollector {

//   inline def collectFromChild[T](fields: ArrayBuffer[String]): Collector =
//     summonFrom {
//       case fc: FieldCollector[T] => fc.collect(fields)
//     }

//   inline def collectFromProduct[Fields <: Tuple, Types <: Tuple](fields: ArrayBuffer[String]): ArrayBuffer[String] = {
//     inline erasedValue[(Fields, Types)] match {
//       case (_: (field *: fields), _: (tpe *: types)) => 
//         collectFromProduct[fields, types](fields.add(constValue[field].toString))
//       case _ =>
//         fields
//     }
//   }

//   inline def derived[T](given ev: Mirror.Of[T], ct: ClassTag[T]): FieldCollector[T] = new FieldCollector[T] {
//     def collect(fields: ArrayBuffer[String]): ArrayBuffer[String] = {
//       inline ev match {
//         case m: Mirror.ProductOf[T] => 
//           collectFromProduct[m.MirroredElemLabels, m.MirroredElemTypes](fields)
//       }
//     }
//   }
// }