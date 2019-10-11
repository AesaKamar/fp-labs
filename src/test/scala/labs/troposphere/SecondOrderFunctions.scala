package labs.troposphere

import cats._
import cats.effect._
import org.scalatest.freespec.AsyncFreeSpec
import cats.implicits._
import org.scalacheck.Prop.forAll
import org.scalatestplus.scalacheck.Checkers._
import org.scalatest.matchers.must.Matchers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._
import scala.util.ChainingSyntax

class SecondOrderFunctions extends AsyncFreeSpec with ChainingSyntax {

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


    "We can call fold on many things; lets see some examples of what they might be and what they do" - {

      case object Apple
      case object Banana

      "List" in {
        val foldedList = List(1, 2, 3)
          .foldl(Apple)((_, _) => Apple)

        foldedList mustBe Apple
      }

      "Option" in {
        val foldedOption = Option("I EXIST").foldl(Apple)((_, _) => Apple)

        foldedOption mustBe Apple
      }

      "Either" in {
        val foldedEither = Either.right(Banana).foldl(Apple)((_, _) => Apple)

        foldedEither mustBe Apple
      }

      "Tree" -  {
        sealed trait Tree[A]
        case class Branch[A](v: A, l: Tree[A], r: Tree[A]) extends Tree[A]
        case class Leaf[A](a: A)                          extends Tree[A]

        implicit def functorTree = new Functor[Tree] {
          override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
            case Branch(v, l, r) => Branch(f(v), map(l)(f), map(r)(f))
            case Leaf(a)        => Leaf(f(a))
          }
        }

        implicit def foldableTree = new Foldable[Tree] {
          override def foldLeft[A, B](fa: Tree[A], b: B)(f: (B, A) => B): B = fa match {
            case Leaf(a) => f(b, a)
            case Branch(v, l, r) =>
              f(b, v)
                .pipe(foldLeft(l, _)(f))
                .pipe(foldLeft(r, _)(f))

          }

          override def foldRight[A, B](fa: Tree[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
        }

        val tree =
          (Branch(1,
            Branch(1,
              Branch(1,
                Leaf(1),
                Leaf(1)),
              Leaf(1)),
            Branch(1,
              Leaf(1),
              Branch(1,
                Leaf(1),
                Leaf(1)))): Tree[Int])

        "this is a more involved example, but follows a similar pattern" in {

          val foldedTree = tree.foldl(0)((a, b) => a + b)

          foldedTree mustEqual 11
        }


        "Lets fold an AppleTree into a basket of apples!" in {
          type Basket[Fruit] = List[Fruit]
          def emptyBasket[Fruit]: Basket[Fruit] = List.empty[Fruit]
          def putFruitInBasket[Fruit](basket: Basket[Fruit], fruit: Fruit ): Basket[Fruit] =
            basket.appended(fruit)

          val appleTree = tree.map(_ => Apple)

          val appleBasket = appleTree.foldl(emptyBasket[Apple.type])(putFruitInBasket)

          appleBasket mustEqual List.fill(11)(Apple)
        }

      }
    }

    "Folds are useful because they allow us to collapse structures into simpler structures: " +
      "think 'summarizing a data structure'" - {

      """
        |                    zero
        |                +-----------+
        |   F[A]      +-->  empt: B  |
        |+---------+  |  +---------+-+
        ||         |  |            |
        ||  e1: A  +--+            |   B
        ||         |  |          +-v----------+
        ||  e2: A  +--+          |  e1: B     |
        ||         |  |          +------------+
        ||  e3: A  +--+
        ||         |
        |+---------+
        |
        |""".stripMargin

      sealed trait A
      case object Apple     extends A
      case object Aubergine extends A
      case object Avocado   extends A

      case class Smoothie(fruits: List[A])

      val listA: List[A] = List(Apple, Aubergine, Avocado)

      val empty: Smoothie = Smoothie(List.empty[A])

      val combine : (Smoothie, A) => Smoothie = (s: Smoothie, a: A)  => Smoothie(s.fruits.appended(a))

      val blendedSmoothie = Smoothie(List(Apple, Aubergine, Avocado))

      "see if you can `map` this code example to the illustration above" in
        (listA.foldl(empty)(combine) mustBe blendedSmoothie)

      "we can say that `foldl` : " +
        "takes an initial B " +
        "and a structure full of As, F[A] " +
        "applies an `arrow`, A => B  to each of the As " +
        "and combined all the Bs together using the supplied operation. " +
        "folds can destroy and 'blend up' the initial structure of the F[_]" - ()
      }
  }

  "CONCLUSION:" - {
    "maps transform types in a structure and preserve the structure" +
      "folds transform types in a structure, collapsing it into a smaller structure" - ()
  }
}
