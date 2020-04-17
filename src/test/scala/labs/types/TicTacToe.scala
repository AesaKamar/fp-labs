package labs.types

object TicTacToe {

  trait Happy
  case object `😄` extends Happy
  case object `😍` extends Happy

  trait Win[A]

  trait XO
  trait X       extends XO
  trait O       extends XO

  type A
  sealed trait Board
  trait `↖`[A <: XO]; trait `↑`[A <: XO]; trait `↗`[A <: XO];

  trait `←`[A <: XO]; trait `△`[A <: XO]; trait `→`[A <: XO];

  trait `↙`[A <: XO]; trait `↓`[A <: XO]; trait `↘`[A <: XO];



  sealed trait                       ThreeConnect[A]
  trait DiagonalConnect[A]   extends ThreeConnect[A]
  trait HorizontalConnect[A] extends ThreeConnect[A]
  trait VerticalConnect[A]   extends ThreeConnect[A]

//  implicit def youWinIf3Connect[xo](implicit ev: ThreeConnect[xo]): Win[xo] = ???
  implicit def youWinIf3Connect[xo : ThreeConnect]: Win[xo] = ???

  // How do ew make an example of a Diagonal Connect?
//  implicit def `Connect⋱`[xo <: XO](implicit `↖` : `↖`[xo], `△` : `△`[xo], `↘` : `↘`[xo]) : DiagonalConnect[xo] = ???
//  implicit def `Connect⋰`[xo <: XO](implicit `↙` : `↙`[xo], `△` : `△`[xo], `↗` : `↗`[xo]) : DiagonalConnect[xo] = ???
  implicit def `becauseConnect⋱`[xo <: XO : `↖` : `△` : `↘`] : DiagonalConnect[xo] = ???
  implicit def `becauseConnect⋰`[xo <: XO : `↙` : `△` : `↗`] : DiagonalConnect[xo] = ???

//  implicit def `Connect⠇  `[xo <: XO](implicit `↖` : `↖`[xo], `←` : `←`[xo], `↙` : `↙`[xo]) : VerticalConnect[xo] = ???
//  implicit def `Connect ⠇ `[xo <: XO](implicit `↑` : `↑`[xo], `△` : `△`[xo], `↓` : `↓`[xo]) : VerticalConnect[xo] = ???
//  implicit def `Connect  ⠇`[xo <: XO](implicit `↗` : `↗`[xo], `→` : `→`[xo], `↘` : `↘`[xo]) : VerticalConnect[xo] = ???
  implicit def `becauseConnect⠇  `[xo <: XO : `↖`: `←`: `↙`] : VerticalConnect[xo] = ???
  implicit def `becauseConnect ⠇ `[xo <: XO : `↑`: `△`: `↓`] : VerticalConnect[xo] = ???
  implicit def `becauseConnect  ⠇`[xo <: XO : `↗`: `→`: `↘`] : VerticalConnect[xo] = ???

//  implicit def `connect˙ ˙ ˙`[xo <: XO](`↖` : `↖`[xo], `↑` : `↑`[xo], `↗` : `↗`[xo]) : HorizontalConnect[xo] = ???
//  implicit def `connect・・・`[xo <: XO](`←` : `←`[xo], `△` : `△`[xo], `→` : `→`[xo]) : HorizontalConnect[xo] = ???
//  implicit def `connect. . .`[xo <: XO](`↙` : `↙`[xo], `↓` : `↓`[xo], `↘` : `↘`[xo]) : HorizontalConnect[xo] = ???
  implicit def `becauseConnect˙ ˙ ˙`[xo <: XO : `↖` : `↑` : `↗`] : HorizontalConnect[xo] = ???
  implicit def `becauseConnect・・・`[xo <: XO : `←` : `△` : `→`] : HorizontalConnect[xo] = ???
  implicit def `becauseConnect. . .`[xo <: XO : `↙` : `↓` : `↘`] : HorizontalConnect[xo] = ???





  // Let's Typecheck if we won yet?

  implicit val m1 = new `↖`[X]{}
  implicit val m2 = new `△`[X]{}
  implicit val m3 = new `↘`[X]{}

  implicit val m4 = new `↖`[O]{}
  implicit val m5 = new `△`[O]{}
  implicit val m6 = new `↘`[O]{}

  implicitly[Win[X]]
  implicitly[Win[O]]


  //---------------
  //- No overlaps
  //---------------
}
