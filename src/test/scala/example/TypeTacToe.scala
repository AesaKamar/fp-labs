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


  "Playing a game" in {


    succeed
  }
}
