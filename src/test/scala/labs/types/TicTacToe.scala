package labs.types

object TicTacToe {


// Implicit search!

trait Exists

val          iExist              : Exists = new Exists {}
implicit val iExistToTheCompiler : Exists = new Exists {}



implicitly[Exists] // ⌘⇧P
























  // TODO
  // Describe how to:
  //  show who the winner of a Game of TicTacToe is to your compiler


  sealed trait Winner[A]


















  // TYPE CHECKING HAPPENS HERE vvv
  implicitly[Winner[_]]
  // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


  trait Player
  trait X extends Player
  trait O extends Player



//  implicit val aWinnerIsX = new Winner[X] {}








  sealed trait PlayerMove
  trait `↖`[A <: Player]; trait `↑`[A <: Player]; trait `↗`[A <: Player];

  trait `←`[A <: Player]; trait `△`[A <: Player]; trait `→`[A <: Player];

  trait `↙`[A <: Player]; trait `↓`[A <: Player]; trait `↘`[A <: Player];




  // TODO
  // Find a way to logically connect a bunch of PlayerMoves with a Winner?












  // How do I know if I won

  sealed trait ThreeConnect[A <: Player]
  trait DiagonalConnect    [A <: Player] extends ThreeConnect[A]
  trait HorizontalConnect  [A <: Player] extends ThreeConnect[A]
  trait VerticalConnect    [A <: Player] extends ThreeConnect[A]







  // TODO
  // Make one of the win conditions imply the Winner
  //
  // If I find evidence of one,
  //   should imply evidence of the other
  implicit val xConnected3Diagonally : ThreeConnect[X] = new DiagonalConnect[X] {}


















//  implicit def youWinIf3Connect[xo](implicit ev: ThreeConnect[xo]): Winner[xo] = ???







// These two are isomorphic!
//  implicit def youWinIf3Connect[xo <: Player : ThreeConnect]: Winner[xo] = ???







// TODO
// Make Bottom 3 imply horizontal connection
//  HorizontalConnect => ThreeConnect => Winner[You!]
//
// `↖`[_] `↑`[_] `↗`[_]
// `←`[_] `△`[_] `→`[_]
// `↙`[X] `↓`[X] `↘`[X]

















  // How do we make an example of a Diagonal Connect?
//  implicit def `becauseConnect⋱`[xo <: Player: `↖`: `△`: `↘`]: DiagonalConnect[xo] = ???
//  implicit def `becauseConnect⋰`[xo <: Player: `↙`: `△`: `↗`]: DiagonalConnect[xo] = ???
//
//  implicit def `becauseConnect⠇  `[xo <: Player: `↖`: `←`: `↙`]: VerticalConnect[xo] = ???
//  implicit def `becauseConnect ⠇ `[xo <: Player: `↑`: `△`: `↓`]: VerticalConnect[xo] = ???
//  implicit def `becauseConnect  ⠇`[xo <: Player: `↗`: `→`: `↘`]: VerticalConnect[xo] = ???
//
//  implicit def `becauseConnect˙ ˙ ˙`[xo <: Player: `↖`: `↑`: `↗`]: HorizontalConnect[xo] = ???
//  implicit def `becauseConnect・・・`[xo <: Player: `←`: `△`: `→`]: HorizontalConnect[xo] = ???
//  implicit def `becauseConnect. . .`[xo <: Player: `↙`: `↓`: `↘`]: HorizontalConnect[xo] = ???

//  // Let's Typecheck if we won yet?
//
  implicit val m1 = new `↖`[X] {}
  implicit val m2 = new `△`[X] {}
  implicit val m3 = new `↘`[X] {}
//
//  implicit val m4 = new `↖`[O] {}
//  implicit val m5 = new `△`[O] {}
//  implicit val m6 = new `↘`[O] {}

  implicitly[Winner[X]]
  implicitly[Winner[O]]
  //---------------
  //- No overlaps
  //---------------
}
