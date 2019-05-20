package example

import org.scalatest.{FreeSpec, Matchers}

import scala.util.ChainingSyntax
import pprint.pprintln

class PipeVsApply extends FreeSpec with Matchers with ChainingSyntax {
  import cats.implicits._

  val sentence = "the quick brown fox jumps over the lazy dog"

  val correctLetterCount = Map(
    't' -> 2,
    'h' -> 2,
    'e' -> 3,
    ' ' -> 8,
    'q' -> 1,
    'u' -> 2,
    'i' -> 1,
    'c' -> 1,
    'k' -> 1,
    'b' -> 1,
    'r' -> 2,
    'o' -> 4,
    'w' -> 1,
    'n' -> 1,
    'f' -> 1,
    'x' -> 1,
    'j' -> 1,
    'm' -> 1,
    'p' -> 1,
    's' -> 1,
    'v' -> 1,
    'l' -> 1,
    'a' -> 1,
    'z' -> 1,
    'y' -> 1,
    'd' -> 1,
    'g' -> 1)

  "letter count should count the letters" ignore {

    sentence.toList
      .map(l => l -> 1)
      .foldl(Map.empty[Char, Int]) {
        case (m, (c, i)) =>
          val i_ = m.getOrElse(c, 0) + i
          m.updated(c, i)
      }
      .should(contain theSameElementsAs correctLetterCount)

  }

  "letter count should count the letters with taps" ignore {

    sentence.toList
      .map(l => l -> 1)
      .tap(pprintln(_))
      .foldl(Map.empty[Char, Int]) {
        case (m, (c, i)) =>
          val i_ = m.getOrElse(c, 0) + i
          m.updated(c, i)
      }
      .tap(pprintln(_))
      .tap(myLetterCount => pprintln(myLetterCount.toSet.diff(correctLetterCount.toSet)))
      .should(contain theSameElementsAs correctLetterCount)
  }

  "pipes should make sense" in {
    trait A
    trait B
    trait C
    trait D
    case object A extends A
    case object B extends B
    case object C extends C
    case object D extends D

    def a2b: A => B = _ => B
    def b2c: B => C = _ => C
    def c2d: C => D = _ => D

    val numAs = 10
    val as: List[A] = List.fill(numAs)(A)

    //We read this right to left with weird loops
    val whatIsThisType = c2d.apply(b2c.apply(a2b.apply(A)))

    //We read this from left to right all the way through
    val thisMakesSense = A.pipe(a2b).pipe(b2c).pipe(c2d)

  }
}
