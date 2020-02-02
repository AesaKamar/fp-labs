package labs.data

import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.freespec.AnyFreeSpec
import cats._
import cats.data._
import org.scalatest.matchers.must.Matchers
import cats.implicits._

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.ChainingSyntax

/**
  * What thee heck is a B-Tree?
  *
  * Its like a binary tree but more than binary
  * Porque solamente los dos?
  *
  * Properties:
  * - Self Balancing
  * - Maintains Sort ordering
  */
object BTreesAndJoins {}

sealed trait BTree[K, V]
final case class Internal[K: Ordering, V](
    // For each K in Map, we push all the elements ordered earlier than each K down to subtrees
    thingsWithLessThanKCanBeFoundHere: Map[K, BTree[K, V]],
    thingsWithMoreThanKCanBeFoundHere: BTree[K, V])
    extends BTree[K, V]
final case class Leaf[K, V](myMapping: Map[K, V]) extends BTree[K, V]

object BTree extends ChainingSyntax {
  val reasonableBranchingFactor: Int = Math.pow(2, 3).toInt

  def insert[K: Ordering, V](bT: BTree[K, V])(k: K, v: V): BTree[K, V] =
    bT match {
      case Leaf(myMapping) if myMapping.size < reasonableBranchingFactor =>
        Leaf(myMapping.updated(k, v))
      case Leaf(myMapping) =>
        val nextLayer =
          myMapping
            .grouped(myMapping.size / reasonableBranchingFactor)
            .map(m => (m.keySet.max, Leaf(m)))
            .toMap
        Internal(nextLayer, Monoid[BTree[K, V]].empty)
      case Internal(lts, gts) if Ordering[K].lt(k, lts.keySet.max) =>
        val (kU, bU) = lts.find { case (mK, mV) => Ordering[K].lt(k, mK) }.get
        Internal(lts.updated(kU, insert(bU)(k, v)), gts)
      case Internal(lts, gts) if Ordering[K].gt(k, lts.keySet.max) =>
        Internal(lts, insert(gts)(k, v))

    }

  def foldLeftWithKeys[K, A, B](fa: BTree[K, A], b: B)(f: (B, (K, A)) => B): B = fa match {
    case Internal(ltKs, gtK) =>
      ltKs.foldLeft(foldLeftWithKeys(gtK, b)(f)) { case (b, (_, bT)) => foldLeftWithKeys(bT, b)(f) }
    case Leaf(myMapping) => myMapping.toList.foldl(b)(f)
  }

  implicit def monoidInstance[K: Ordering, V]: Monoid[BTree[K, V]] = new Monoid[BTree[K, V]] {
    override def empty: BTree[K, V] = Leaf(Map.empty)
    override def combine(x: BTree[K, V], y: BTree[K, V]): BTree[K, V] = (x, y) match {
      case (Leaf(mA), Leaf(mB)) if mA.size + mB.size <= reasonableBranchingFactor =>
        Leaf(mA <+> mB)
      case (Leaf(mA), Leaf(mB)) if mA.size + mB.size > reasonableBranchingFactor =>
        (mA <+> mB).foldLeft(empty) { case (b, (k, v)) => insert(b)(k, v) }
      case (a, b) => foldLeftWithKeys(b, a) { case (acc, (k, v)) => insert(acc)(k, v) }
    }
  }

  implicit def functorInstance[K: Ordering] = new Functor[BTree[K, *]] {
    override def map[A, B](fa: BTree[K, A])(f: A => B): BTree[K, B] = fa match {
      case Internal(lts, gts) =>
        Internal(lts.map { case (k, v) => (k, map(v)(f)) }, map(gts)(f))
      case Leaf(mapping) => Leaf(mapping.map { case (k, v) => (k, f(v)) })
    }
  }

  implicit def foldableInstance[K: Ordering] = new Foldable[BTree[K, *]] {
    override def foldLeft[A, B](fa: BTree[K, A], b: B)(f: (B, A) => B): B = fa match {
      case Internal(ltKs, gtK) =>
        ltKs.foldLeft(foldLeft(gtK, b)(f)) { case (b, (_, bT)) => foldLeft(bT, b)(f) }
      case Leaf(myMapping) => myMapping.values.toList.foldl(b)(f)
    }

    override def foldRight[A, B](fa: BTree[K, A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
  }

}

class BTreesAndJoinsTests extends AnyFreeSpec with Matchers with ChainingSyntax {

  "Monoid" - {
    "empty leaf node must contain no elements" in {
      Monoid[BTree[String, Int]].empty mustBe Leaf(Map.empty)
    }

    "combining leaf nodes which are empty" in {
      Monoid[BTree[String, Int]].empty.combine(Monoid[BTree[String, Int]].empty) mustBe Monoid[BTree[String, Int]].empty
    }
    "combining leaf nodes which are smaller than reasonableBranchingFactor" in {
      (Leaf(Map(1 -> "a")): BTree[Int, String])
        .combine(Leaf(Map(2 -> "b"))) mustBe Leaf(Map(1 -> "a", 2 -> "b"))
    }
    "combining way too full of leaves to be reasonable" in {
      val res1 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(15).take(30).map(x => (x, x)).toMap))

      val res2 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(15).map(x => (x, x)).toMap))

      pprint.log(res1, height = 1000)
      pprint.log(res1.foldl(Set.empty[Int])(_ + _))
      pprint.log(res2, height = 1000)
      pprint.log(res2.foldl(Set.empty[Int])(_ + _))

      pprint.log(res2.combine(res1), height = 1000)
      pprint.log(res1.combine(res2), height = 1000)
      pprint.log(res2.combine(res1).foldl(Set.empty[Int])(_ + _))
      pprint.log(res1.combine(res2).foldl(Set.empty[Int])(_ + _))

      succeed
    }
  }

  "Insert" - {
    "inserting into empty" in {
      Monoid[BTree[String, String]].empty.pipe(BTree.insert(_)("hey", "y'all")) mustBe Leaf(Map("hey" -> "y'all"))
    }
  }

}