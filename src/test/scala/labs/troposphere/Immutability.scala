package labs.troposphere

import scala.util.ChainingSyntax
import org.scalatest.freespec.AsyncFreeSpec

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

    object TheListSortingMan {
      class SortedList private[TheListSortingMan](list: List[Int]){
        def toList = list
      }
      def sort(list: List[Int]): SortedList = new SortedList(list.sorted)
    }

    "Introducing TheListSortingMan, he's got one job" in {
      val unsortedList = (20 to (1, -1)).toList
      TheListSortingMan.sort(unsortedList).toList mustEqual (1 to 20)
    }

    "TheListSortingMan is uniquely qualified to sort lists. " - {
      "When he sorts a List, he produces a new type called a SortedList" in {
        val input: List[Int] = List(1, 2, 3)
        val output = TheListSortingMan.sort(input)

        output mustBe a [TheListSortingMan.SortedList]
        output mustNot be(a[List[_]])
      }
    }
    "Nobody else can make an instance of SortedList. " +
      "Go ahead and try it!" in  {
      //val arghICantDoThis = new TheListSortingMan.SortedList(Nil)
      succeed
    }

    "SortedList is always guaranteed to be sorted, since: " +
      "TheListSortingMan always produces SortedLists which are sorted, " +
      "and no one else can make a SortedList" in {
      succeed
    }

  }

}
