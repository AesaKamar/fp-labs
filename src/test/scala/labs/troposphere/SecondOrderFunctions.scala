package labs.troposphere

import cats.effect._
import org.scalatest.freespec.AsyncFreeSpec
import cats.implicits._
import org.scalacheck.Prop.forAll
import org.scalatestplus.scalacheck.Checkers._
import org.scalatest.matchers.must.Matchers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._

class SecondOrderFunctions extends AsyncFreeSpec {

  "What is mapping? " - {
    "https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/Functor.scala" - ()

    case object Apple
    "We can call map on many things; lets see some examples of what they might be and what they do" - {

      "List" in {
        val mappedList = List(1, 2, 3).map(_ => Apple)

        mappedList mustBe List(Apple, Apple, Apple)
      }

      "Option" in {
        val mappedOption = Option(1).map(_ => Apple)

        mappedOption mustBe Option(Apple)
      }

      "Future (This one is weird because futures)" in {

        val mappedFuture = Future.successful(1).map(_ => Apple)(Implicits.global)

        Await.result(mappedFuture, 1 second) mustEqual Apple
      }

      "IO" in {
        val mappedIO = IO(1).map(_ => Apple)

        mappedIO.unsafeRunSync() mustEqual Apple
      }

      "That's neat, but kind of seems obvious;" +
        " what's the common pattern?" - ()

      "the map function operates on `Types` that contain things" - {
        "you can't have just a List" - ()
        //val thisIsNotAValidType : List

        "you can't have just an Option" - ()
        //val thisIsNotAValidTypeEither : Option

        "but you can have a List of Strings" - ()
        val thisIsAValidType: List[String] = List("a string")
        "or an Option of Banana" - ()
        trait Banana
        case object Banana extends Banana
        val thisIsAlsoAValidType: Option[Banana] = Some(Banana)
      }
    }

    "This still seems really obvious, its not super complicated or hard to think about. " +
      "What does this buy us?" - {

      "`map` is always * Structure-Preserving *" - {

        "If I have a list of one element, and I call map, I always end up with one element" in {
          List(1).map(_ => Apple).length mustBe 1
        }

        "If I have a list with no elements, `map` doesn't have the power to add any new structure " +
          "where it didn't already exist" in {
          List.empty[Unit].map(_ => Apple).isEmpty mustBe true
        }

        "If I have an Option that is a Some, it is NEVER possible for me " +
          "to lose information via any sequence of maps" in {

          trait Banana
          case object Banana extends Banana
          val optionThing: Option[Banana] = None

          optionThing
            .map(_ => Banana)
            .map(_ => Banana)
            .map(_ => Banana)
            .map(_ => Banana)
            .map(_ => Banana) mustBe None
        }

      }
    }

    "Is there a more satisfying intuition to understand the way `map` works " - {

      """  List[A]                  List[B]
        |+---------+             +----------+
        ||         |             |          |
        ||  e1: A  +------------->  e1: B   |
        ||         |   A => B    |          |
        ||  e2: A  +------------->  e2: B   |
        ||         |             |          |
        ||  e3: A  +------------->  e3: B   |
        ||         |             |          |
        |+---------+             +----------+
        |""".stripMargin

      sealed trait A
      case object Apple     extends A
      case object Aubergine extends A
      case object Avocado   extends A

      sealed trait B
      case object Banana     extends B
      case object BreadFruit extends B
      case object BroadBean  extends B

      val listA: List[A] = List(Apple, Aubergine, Avocado)

      val a2b: A => B = {
        case Apple     => Banana
        case Aubergine => BreadFruit
        case Avocado   => BroadBean
      }

      val listB: List[B] = List(Banana, BreadFruit, BroadBean)

      "see if you can `map` this code example to the illustration above" in
        (listA.map(a2b) mustBe listB)

      "we can say that `map` : " +
        "applies an `arrow`, A => B " +
        "to a structure full of As, F[A] " +
        "and returns a structure full of Bs, F[B]" +
        "while leaving the structure in-tact, F[_]" - ()
    }
  }

  "What is folding? " - {
    "https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/Foldable.scala" - ()
  }

  "CONCLUSION:" - {
    "maps transform types in a structure and preserve the structure" +
      "folds transform types in a structure, collapsing it into a smaller structure" - ()
  }
}
