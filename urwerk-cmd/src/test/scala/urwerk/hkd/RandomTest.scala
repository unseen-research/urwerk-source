package urwerk.hkd

import urwerk.hkd.SiteMember.RegisteredUser
import urwerk.test.TestBase

import scala.deriving.Mirror
import scala.util.Random as ScalaRandom

case class Details(info: String, age: Long)
enum SiteMember:
  case RegisteredUser(id: Long, email: String, isAdmin: Boolean, details: Details)
  case AnonymousUser(session: String)

//sealed trait SiteMember
//case class RegisteredUser(id: Long, email: String, isAdmin: Boolean) extends SiteMember
//case class AnonymousUser(session: String) extends SiteMember

given randStr: Random[String] with
  def generate(): String = ScalaRandom.alphanumeric.take(5).mkString

given randLong: Random[Long] with
  def generate(): Long = ScalaRandom.nextLong(1000000)

given randBool: Random[Boolean] with
  def generate(): Boolean = ScalaRandom.nextBoolean()

class RandomTest extends TestBase:

  inline def rand[A](using r: Random[A]): A =
    r.generate()

  "summon random" in {
    summon[Random[SiteMember]].generate()
  }

  "summon 2" in {
    import SiteMember.RegisteredUser

    println(rand[RegisteredUser])
    println("xxxx")
    println(summon[Random[RegisteredUser]].generate())
    println(summon[Random[SiteMember]].generate())
    println(summon[Random[SiteMember]].generate())
    println(summon[Random[SiteMember]].generate())
  }
