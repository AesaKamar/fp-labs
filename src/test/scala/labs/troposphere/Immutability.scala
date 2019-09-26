package labs.troposphere

import scala.util.ChainingSyntax
import org.scalatest.freespec.AsyncFreeSpec
import org.scalacheck.Prop.forAll
import org.scalatestplus.scalacheck.Checkers._

class Immutability extends AsyncFreeSpec with ChainingSyntax {
  import org.scalatest.matchers.must.Matchers._

  "We say that we want code to be `easy to reason about`; " +
    "all programs take inputs and return outputs" - {

    "As a `programmer`, your job is to compose big programs out of small programs" - {

      val smallList = (1 to 5)
      val smallList2 = (6 to 10)

      val bigList = smallList.concat(smallList2).toList

      "When we compose functions, we expect them to work a certain way" in {
        bigList mustEqual (1 to 10).toList
      }
    }

    "We know that composing big programs out of smaller programs is safe because the smaller programs are well behaved" - {
      val smallList = (1 to 5)
      val smallList2 = (6 to 10)
      "If the starting values are the same" in {
        smallList mustEqual (1 to 5)
        smallList2 mustEqual (6 to 10)
      }

      val bigList = smallList.concat(smallList2).toList
      "then the output should be the same" in {
        bigList mustEqual (1 to 10).toList

      }
    }
  }

  "Lets move onto an example of why Immutability might be useful in helping us understand our programs better" +
    "" +
    ""

  object TheListSortingMan {

    class SortedList private[TheListSortingMan] (val list: List[Int])
    def sort(list: List[Int]): SortedList = new SortedList(list.sorted)
  }

  "Introducing TheListSortingMan, he's got one job" in {
    val unsortedList = (20 to (1, -1)).toList
    TheListSortingMan.sort(unsortedList).list mustEqual (1 to 20)
  }

  "TheListSortingMan is uniquely qualified to sort lists. " - {
    "When he sorts a List, he produces a new type called a SortedList" in {
      val input: List[Int] = List(1, 2, 3)
      val output = TheListSortingMan.sort(input)

      output mustBe a[TheListSortingMan.SortedList]
      output mustNot be(a[List[_]])
    }
  }
  "Nobody else can make an instance of SortedList. " +
    "Go ahead and try it!" in {
    //val arghICantDoThis = new TheListSortingMan.SortedList(Nil)
    succeed
  }

  def isThisImmutableSortedListSorted(s: TheListSortingMan.SortedList) = isSorted(s.list)

  def isSorted[A: Ordering](s: List[A]): Boolean = s match {
    case Nil     => true
    case List(_) => true
    case _       => s.sliding(2).forall { case List(x, y) => implicitly[Ordering[A]].lteq(x, y) }
  }

  "SortedList is always guaranteed to be sorted, since: " +
    "TheListSortingMan always produces SortedLists which are sorted, " +
    "and no one else can make a SortedList" in {
    check(forAll { randomList: List[Int] =>
      isSorted(TheListSortingMan.sort(randomList).list)
    })
  }

  "Is the SortedList _really_ always sorted? " +
    "Lets see if we can make the same guarantees about our lists being sorted with TheMutableListSortingMan" - {

    object TheMutableListSortingMan {

      class SortedList private[TheMutableListSortingMan] (var list: List[Int])

      def sort(list: List[Int]): SortedList = new SortedList(list.sorted)
    }
    def isThisMutableSortedListSorted(s: TheMutableListSortingMan.SortedList) = isSorted(s.list)

    val variableSortedList = TheMutableListSortingMan.sort(List(3, 2, 1))

    "if we are given the ability to mutate data, we can invalidate the properties weeworked so hard to set up" in {
      variableSortedList.list = List(8, 3, 1)
      isThisMutableSortedListSorted(variableSortedList) mustBe false
    }

    "Lets simulate some pesky external agent who might modify our data" - {
      def seemsFineAndSafeToRunThisFunction(sortedList: TheMutableListSortingMan.SortedList): Unit =
        sortedList.list = sortedList.list :+ 0

      "If we use the Mutable version, we can invalidate our guarantee about SortedList! " +
        "It becomes possible to make an instance of SortedList which fails to pass the isSorted test! " ignore {
        check(forAll { randomList: List[Int] =>
          var sortedList = TheMutableListSortingMan.sort(randomList)

          seemsFineAndSafeToRunThisFunction(sortedList)

          isThisMutableSortedListSorted(sortedList)
        })
      }

      "If we use the Immutable version, we are no longer able to invalidate the properties that we set up in out Type System" in {
        check(forAll { randomList: List[Int] =>
          val sortedList = TheListSortingMan.sort(randomList)

          //sortedList.list = sortedList.list :+ 0

          isThisImmutableSortedListSorted(sortedList)
        })
      }

    }
  }

  "CONCLUSION:" - {
    "Because mutability allows us to invalidate properties we set into our Type Systems at runtime, " +
      "And acknowledging that we want our programs to be unsurprising, where runtime behavior can be described at compile-time, " +
      "We choose to forgo mutability in favor of programs which are easy to reason about" in succeed
  }

}
