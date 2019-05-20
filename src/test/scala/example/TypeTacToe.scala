package example

import org.scalatest.{AsyncFreeSpec, Matchers}

class TypeTacToe extends AsyncFreeSpec with Matchers {
  import cats.implicits._
  import scala.util.chaining._

  /*
  A tic tac toe board
       _|_|_
       _|_|_
        | |
  Win by getting 3 in a row!
   */

  trait Win[A]

  sealed trait X
  case object X extends X
  sealed trait O
  case object O extends X

  trait ThreeConnect[A]
  trait DiagonalConnect[A]
  trait HorizontalConnect[A]
  trait VerticalConnect[A]

  trait N[A]
  trait NE[A]
  trait E[A]
  trait SE[A]
  trait S[A]
  trait SW[A]
  trait W[A]
  trait NW[A]
  trait C[A]

  // How do you win though?
  implicit def youWinIf3Connect[A](implicit ev: ThreeConnect[A]): Win[A] = ???

  //How do you get threeConnect?
  implicit def threeConnectIfDiagonal[A](implicit ev: DiagonalConnect[A]):     ThreeConnect[A] = ???
  implicit def threeConnectIfHorizontal[A](implicit ev: HorizontalConnect[A]): ThreeConnect[A] = ???
  implicit def threeConnectIfVertical[A](implicit ev: VerticalConnect[A]):     ThreeConnect[A] = ???

  //How do you know if vertical connect?
  implicit def verticalConnectIfAllRight[A](implicit evNE: NE[A], evE: E[A], evSE: SE[A]): VerticalConnect[A] = ???
  implicit def verticalConnectIfAllCenter[A](implicit evN: N[A], evC: C[A], evS: S[A]):    VerticalConnect[A] = ???
  implicit def verticalConnectIfAllLeft[A](implicit evNW: NW[A], evW: W[A], evSW: SW[A]):  VerticalConnect[A] = ???

  //How do you know if Horizontal connect?
  implicit def horizontalConnectIfAllTop[A](implicit evNW: NW[A], evN: N[A], evNE: NE[A]):    HorizontalConnect[A] = ???
  implicit def horizontalConnectIfAllCenter[A](implicit evW: W[A], evC: C[A], evE: E[A]):     HorizontalConnect[A] = ???
  implicit def horizontalConnectIfAllBottom[A](implicit evNW: NW[A], evW: W[A], evSW: SW[A]): HorizontalConnect[A] = ???

  //How do you know if Diagonal connect?
  implicit def diagonalConnectIfForwardSlash[A](implicit evSW: SW[A], evC: C[A], evNE: NE[A]): DiagonalConnect[A] = ???
  implicit def diagonalConnectIfBackSlash[A](implicit evNW: NW[A], evC: C[A], evSE: SE[A]):    DiagonalConnect[A] = ???

  "Playing a game" in {

//    implicit val move1: N[O] = ???
//    implicit val move2: C[O] = ???
//    implicit val move3: S[O] = ???
//
//    implicit val move4: N[X] = ???
//    implicit val move5: C[X] = ???
//    implicit val move6: S[X] = ???
//
//    implicitly[Win[X]]
//    implicitly[Win[O]]
    succeed
  }
}
