package example

import org.scalatest.{AsyncFreeSpec, Matchers}

import scala.util.ChainingSyntax

class MapMergeMonoid extends AsyncFreeSpec with Matchers with ChainingSyntax {
  import cats.implicits._

  val m1   = Map('a' -> 1, 'b' -> 1, 'c' -> 1)
  val m2   = Map('a' -> 2, 'b' -> 2, 'c' -> 2)
  val mSum = Map('a' -> 3, 'b' -> 3, 'c' -> 3)

  "Merge using standard functions" in {

    def updateWith[K, V](m: Map[K, Int], k: K, v: Int): Map[K, Int] =
      m.get(k) match {
        case Some(value) => m.updated(k, value + v)
        case None        => m
      }

    def merge(a: Map[Char, Int], b: Map[Char, Int]): Map[Char, Int] =
      b.foldLeft(a) { case (m, (k, v)) => m.pipe(updateWith(_, k, v)) }

    merge(m1, m2) shouldBe mSum
  }

}
