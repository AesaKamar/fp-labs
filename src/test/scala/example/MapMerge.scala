package example

import cats.kernel.Semigroup
import org.scalatest.{AsyncFreeSpec, Matchers}

import scala.util.ChainingSyntax

class MapMerge extends AsyncFreeSpec with Matchers with ChainingSyntax {
  import cats.implicits._

  val m1   = Map('a' -> 1, 'b' -> 1, 'c' -> 1)
  val m2   = Map('a' -> 2, 'b' -> 2, 'c' -> 2)
  val mSum = Map('a' -> 3, 'b' -> 3, 'c' -> 3)

  val _m1   = Map('a' -> 1, 'b' -> 1, 'c' -> 1)
  val _m2   = Map('a' -> 2, 'd' -> 2, 'c' -> 2)
  val _mSum = Map('a' -> 1, 'b' -> 1, 'd' -> 2, 'c' -> 1)

  def merge[K, V : Semigroup](
    a: Map[K, V],
    b: Map[K, V]
  ): Map[K, V] =
      b.foldLeft(a) { case (m, (k, v)) => update(m, (k, v))}

  def merge[K, V](
    a: Map[K, V],
    b: Map[K, V]
  )(implicit ev: Semigroup[V]): Map[K, V] =
      b.foldLeft(a) { case (m, (k, v)) => update(m, (k, v))}

  private def update[K, V : Semigroup](
    m: Map[K, V],
    kv: (K, V),
  ): Map[K, V] = {
    m.get(kv._1) match {
      case Some(v) => m.updated(kv._1, v.combine( kv._2))
      case None =>
        m.updated(kv._1, kv._2)
    }
  }


  "Merge using standard functions" in {
    implicit val semigroupInt : Semigroup[Int] = (a, b) => a + b
    merge(m1, m2)(semigroupInt) shouldBe mSum
  }

  "Merge the other way" in {
    implicit val semigroupInt : Semigroup[Int] = (a, _) => a
    merge(_m1, _m2)(semigroupInt) shouldBe _mSum
  }

}
