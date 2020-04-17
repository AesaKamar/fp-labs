package labs.data

import org.scalatest.freespec.AnyFreeSpec
import cats._
import cats.data._
import org.scalatest.matchers.must.Matchers
import cats.implicits._
import org.scalatest.{Assertion, Inside}

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
sealed trait BTree[K, V]
final case class Internal[K: Ordering, V](
    // For each K in Map, we push all the elements ordered earlier than each K down to subtrees
    maxK: K,
    thingsWithLessThanKCanBeFoundHere: Map[K, BTree[K, V]],
    thingsWithMoreThanKCanBeFoundHere: BTree[K, V])
    extends BTree[K, V]
final case class Leaf[K, V](myMapping: Map[K, V]) extends BTree[K, V]

object BTree extends ChainingSyntax {
  val reasonableBranchingFactor: Int = Math.pow(2, 3).toInt

  def empty[K, V]: BTree[K, V] = Leaf(Map.empty[K, V])

  def insert[K, V](bT: BTree[K, V])(k: K, v: V)(implicit ord: Ordering[K]): BTree[K, V] = {
    import ord.mkOrderingOps
    bT match {
      case Leaf(myMapping) if myMapping.size < reasonableBranchingFactor =>
        Leaf(myMapping.updated(k, v))
      case Leaf(myMapping) if myMapping.size == reasonableBranchingFactor && k >= myMapping.keySet.max =>
        val nextLayer =
          myMapping
            .grouped(myMapping.size / reasonableBranchingFactor)
            .map(m => (m.keySet.max, Leaf(m)))
            .toMap
        Internal(myMapping.keySet.max, nextLayer, Leaf(Map(k -> v)))
      case Leaf(myMapping) if myMapping.size == reasonableBranchingFactor && k < myMapping.keySet.max =>
        val updatedMapping = myMapping.updated(k, v)
        val nextLayer =
          updatedMapping
            .grouped(updatedMapping.size / (reasonableBranchingFactor / 2))
            .map(m => (m.keySet.max, Leaf(m)))
            .toMap
        Internal(updatedMapping.keySet.max, nextLayer, Leaf(Map()))
      case Internal(maxK, lts, gts) if k <= lts.keySet.max =>
        val (kU, bU) = lts.find { case (mK, mV) => k <= mK }.get
        Internal(maxK, lts.updated(kU, insert(bU)(k, v)), gts)
      case Internal(maxK, lts, gts) if k > lts.keySet.max =>
        Internal(k, lts, insert(gts)(k, v))

    }
  }

  def foldLeftWithKeys[K, A, B](fa: BTree[K, A], b: B)(f: (B, (K, A)) => B): B = fa match {
    case Internal(maxK, ltKs, gtK) =>
      ltKs.foldLeft(foldLeftWithKeys(gtK, b)(f)) { case (b, (_, bT)) => foldLeftWithKeys(bT, b)(f) }
    case Leaf(myMapping) => myMapping.toList.foldl(b)(f)
  }

  implicit def monoidInstance[K: Ordering, V]: Monoid[BTree[K, V]] = new Monoid[BTree[K, V]] {
    override def empty: BTree[K, V] = BTree.empty[K, V]
    override def combine(x: BTree[K, V], y: BTree[K, V]): BTree[K, V] = (x, y) match {
      case (a @ Leaf(mA), b @ Leaf(mB)) if mA.size + mB.size <= reasonableBranchingFactor =>
        Leaf(mA <+> mB)
      case (a @ Leaf(mA), b @ Leaf(mB)) if mA.size + mB.size > reasonableBranchingFactor =>
        (mA <+> mB).foldLeft(empty) { case (b, (k, v)) => insert(b)(k, v) }
      case (a, b) =>
        foldLeftWithKeys(a, b) { case (acc, (k, v)) => insert(acc)(k, v) }
    }
  }

  implicit def functorInstance[K: Ordering]: Functor[BTree[K, *]] = new Functor[BTree[K, *]] {
    override def map[A, B](fa: BTree[K, A])(f: A => B): BTree[K, B] = fa match {
      case Internal(maxK, lts, gts) =>
        Internal(maxK, lts.map { case (k, v) => (k, map(v)(f)) }, map(gts)(f))
      case Leaf(mapping) => Leaf(mapping.map { case (k, v) => (k, f(v)) })
    }
  }

  implicit def foldableInstance[K: Ordering]: Foldable[BTree[K, *]] = new Foldable[BTree[K, *]] {
    override def foldLeft[A, B](fa: BTree[K, A], b: B)(f: (B, A) => B): B = fa match {
      case Internal(maxK, ltKs, gtK) =>
        ltKs.foldLeft(foldLeft(gtK, b)(f)) { case (b, (_, bT)) => foldLeft(bT, b)(f) }
      case Leaf(myMapping) => myMapping.values.toList.foldl(b)(f)
    }
    override def foldRight[A, B](fa: BTree[K, A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
  }

}

class BTreesAndJoinsTests extends AnyFreeSpec with Matchers with Inside with ChainingSyntax {

  "Monoid" - {
    "empty leaf node must contain no elements" in {
      Monoid[BTree[String, Int]].empty mustBe Leaf(Map.empty)
    }

    "combining leaf nodes which are empty" in {
      Monoid[BTree[String, Int]].empty.combine(Monoid[BTree[String, Int]].empty) mustBe Monoid[BTree[String, Int]].empty
    }
    "combining leaf nodes which are smaller than reasonableBranchingFactor" in {
      (Leaf(Map(1 -> "a")): BTree[Int, String])
        .combine(Leaf(Map(2 -> "b"))).tap(pprint.log(_)) mustBe Leaf(Map(1 -> "a", 2 -> "b"))
    }
    "foldLeftWithKeys should resolve into a list containing all elements" in {
      val tree = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(5).map(x => (x, x)).toMap))

      BTree.foldLeftWithKeys(tree, List.empty[(Int, Int)]) { case (b, (k, a)) => b.appended((k, a)) } must contain theSameElementsAs
        LazyList.from(0).take(5).map(x => (x, x))
    }

    "combining lists" in {
      val res1 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(8).map(x => (x, x)).toMap))

      val res2 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(8).take(8).map(x => (x, x)).toMap))

      res1.combine(res2).foldl(Set.empty[Int]) { case (a, b) => a.+(b) } mustEqual (0 until 16).toSet
    }

    "combining really big lists" in {
      val res1 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(50).map(x => (x, x)).toMap))

      val res2 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(50).take(50).map(x => (x, x)).toMap))

      res1.combine(res2).foldl(Set.empty[Int]) { case (a, b) => a.+(b) } mustEqual (0 until 100).toSet
    }

  }

  "Insert" - {
    "inserting into empty" in {
      Monoid[BTree[String, String]].empty.pipe(BTree.insert(_)("hey", "y'all")) mustBe Leaf(Map("hey" -> "y'all"))
    }
    "bTrees should keep things less than maxK in the lessThanK and greater than maxK in the greatherThanK" in {
      def validate[K, V](bTree: BTree[K, V])(implicit ord: Ordering[K]): Unit = {
        import ord.mkOrderingOps
        bTree match {
          case Internal(maxK, thingsWithLessThanKCanBeFoundHere, thingsWithMoreThanKCanBeFoundHere) =>
            inside(thingsWithLessThanKCanBeFoundHere)(_.keySet.max must (be <= maxK))
            inside(thingsWithMoreThanKCanBeFoundHere)(
              BTree.foldLeftWithKeys(_, Set.empty[K]) { case (b, (k, v)) => b + k }.maxOption.map(_ must (be >= maxK)))
            validate(thingsWithMoreThanKCanBeFoundHere)
            thingsWithLessThanKCanBeFoundHere.foreach(x => validate(x._2))

          case Leaf(myMapping) => ()
        }
      }

      val res1 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(50).map(x => (x, x)).toMap))

      val res2 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(50).take(50).map(x => (x, x)).toMap))

      validate(res1.combine(res2))
    }

    "bTree internal nodes should not have more than the reasonableBranchingFactor number of internal nodes" in {
      def validate[K, V](bTree: BTree[K, V])(implicit ord: Ordering[K]): Unit = {
        import ord.mkOrderingOps
        bTree match {
          case Internal(maxK, thingsWithLessThanKCanBeFoundHere, thingsWithMoreThanKCanBeFoundHere) =>
            inside(thingsWithLessThanKCanBeFoundHere)(_.size must be <= BTree.reasonableBranchingFactor)
            thingsWithLessThanKCanBeFoundHere.foreach(x => validate(x._2))
          case Leaf(myMapping) =>
            inside(myMapping)(_.size must be <= BTree.reasonableBranchingFactor)
            ()
        }
      }
      val res1 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(0).take(50).map(x => (x, x)).toMap))

      val res2 = (Leaf(Map.empty): BTree[Int, Int])
        .combine(Leaf(LazyList.from(50).take(50).map(x => (x, x)).toMap))

      validate(res1.combine(res2).tap(pprint.log(_, height =1000)))

    }
  }

  "visualizing bTrees that point to pages of records" in {

    val emptyPageTree = BTree.empty[Char, List[String]]

    val indexedPages = ('a' to 'z').map(char => char -> List.fill(10)(char.toString)).toList

    val completeBTree = indexedPages.foldl(emptyPageTree) {
      case (bTree, (char, page)) => BTree.insert(bTree)(char, page)
    }

//    pprint.log(completeBTree)

    succeed

  }

}
