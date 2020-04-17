package labs.types

import org.scalatest.freespec.AsyncFreeSpec
import simulacrum._

import scala.util.ChainingSyntax

sealed trait Not[A] {
  override def toString: String = "Not"
}

//Typelevel not
object Not {
  private val notInstance = new Not[Any] {}
  implicit def makeNot[A]: Not[A] = notInstance.asInstanceOf[Not[A]]
  implicit def makeNotAmbig[A](implicit a: A): Not[A] = sys.error("this method should never be called")
}

class FourColorMap extends AsyncFreeSpec with ChainingSyntax {}

//sealed trait StateOfAustralia
//sealed trait            NorthernTerritory                   extends StateOfAustralia
//
//sealed trait                            Queensland          extends StateOfAustralia
//sealed trait WesternAustralia                               extends StateOfAustralia
//
//sealed trait            SouthAustralia                      extends StateOfAustralia
//sealed trait                            NewSouthWales         extends StateOfAustralia
//sealed trait                            Victoria           extends StateOfAustralia
//
//sealed trait                            Tasmania           extends StateOfAustralia

sealed trait StateOfAustralia
sealed trait NorthernTerritory extends StateOfAustralia
sealed trait Queensland        extends StateOfAustralia
sealed trait WesternAustralia  extends StateOfAustralia
sealed trait SouthAustralia    extends StateOfAustralia
sealed trait NewSouthWales     extends StateOfAustralia
sealed trait Victoria          extends StateOfAustralia
sealed trait Tasmania          extends StateOfAustralia

sealed trait Color
case object Orange extends Color
case object Cyan   extends Color
case object White  extends Color
case object Lime   extends Color

class Adjacency[A <: StateOfAustralia, B <: StateOfAustralia]
class Coloring[A <: StateOfAustralia, C <: Color]

class InvalidColoring[A <: StateOfAustralia, B <: StateOfAustralia, C <: Color]

object Australia {
  implicit val `72ef9032-38e3-4fa1-9bba-9c7f879e698f` =
    new Adjacency[WesternAustralia, NorthernTerritory]
  implicit val `80103ec9-1962-4e88-b19a-b21a84efe188` =
    new Adjacency[WesternAustralia, SouthAustralia]
  implicit val `712658e5-b060-4291-b02b-0cbafe675f2e` =
    new Adjacency[NorthernTerritory, SouthAustralia]
  implicit val `e37229f7-7a02-401f-8c7e-1020f423a139` =
    new Adjacency[NorthernTerritory, Queensland]
  implicit val `bf4d3a6d-8168-428d-a77c-5b5c728c8d93` =
    new Adjacency[SouthAustralia, Queensland]
  implicit val `f8837668-9d64-44dc-9077-e4c7aba03b77` =
    new Adjacency[SouthAustralia, NewSouthWales]
  implicit val `507737f3-6444-47ca-b643-fee0bda1673e` =
    new Adjacency[SouthAustralia, Victoria]
  implicit val `bc31a0b5-5ffd-4484-a763-07845f7b3b95` =
    new Adjacency[Queensland, NewSouthWales]
  implicit val `204b51d2-c185-4462-89d4-d4147337b0ad` =
    new Adjacency[NewSouthWales, Victoria]

  //Typelevel implication
  implicit def `Adjacency[A,B] <=> Adjacency[B,A]`[A <: StateOfAustralia, B <: StateOfAustralia](
      adjacency: Adjacency[A, B]): Adjacency[B, A] =
    new Adjacency[B, A]




  implicit def `adjacencyWithOneColoringIsInvalid`[A <: StateOfAustralia, B <: StateOfAustralia, C <: Color](
      implicit
      ev: Adjacency[A, B],
      coloringA: Coloring[A, C],
      coloringB: Coloring[B, C]): InvalidColoring[A, B, C] = new InvalidColoring[A, B, C]


  class ValidlyColoredMapOfAustralia[
      NT <: Color,
      QLD <: Color,
      WA <: Color,
      SA <: Color,
      NSW <: Color,
      VIC <: Color,
      TAS <: Color](
      implicit
      someColoringForNorthernTerritory: Coloring[NorthernTerritory, NT],
      someColoringForQueensland: Coloring[Queensland, QLD],
      someColoringForWesternAustralia: Coloring[WesternAustralia, WA],
      someColoringForSouthAustralia: Coloring[SouthAustralia, SA],
      someColoringForNewSouthWales: Coloring[NewSouthWales, NSW],
      someColoringForVictoria: Coloring[Victoria, VIC],
      someColoringForTasmania: Coloring[Tasmania, TAS],
      validColoringBetweenWesternAustraliaNorthernTerritory: Not[InvalidColoring[WesternAustralia, NorthernTerritory, WA]],
      validColoringBetweenWesternAustraliaSouthAustralia: Not[InvalidColoring[WesternAustralia, SouthAustralia, WA]],
      validColoringBetweenNorthernTerritorySouthAustralia: Not[InvalidColoring[NorthernTerritory, SouthAustralia, NT]],
      validColoringBetweenNorthernTerritoryQueensland: Not[InvalidColoring[NorthernTerritory, Queensland, NT]],
      validColoringBetweenSouthAustraliaQueensland: Not[InvalidColoring[SouthAustralia, Queensland, SA]],
      validColoringBetweenSouthAustraliaNewSouthWales: Not[InvalidColoring[SouthAustralia, NewSouthWales, SA]],
      validColoringBetweenSouthAustraliaVictoria: Not[InvalidColoring[SouthAustralia, Victoria, SA]],
      validColoringBetweenQueenslandNewSouthWales: Not[InvalidColoring[Queensland, NewSouthWales, QLD]],
      validColoringBetweenNewSouthWalesVictoria: Not[InvalidColoring[NewSouthWales, Victoria, NSW]])


  implicit val colorNT = new Coloring[NorthernTerritory, White.type ]
  implicit val colorQLD = new Coloring[Queensland, Cyan.type ]
  implicit val colorWA = new Coloring[WesternAustralia, Orange.type ]
  implicit val colorSA = new Coloring[SouthAustralia, Lime.type ]
  implicit val colorNSW = new Coloring[NewSouthWales, Orange.type ]
  implicit val colorVIC = new Coloring[Victoria, Cyan.type ]
  implicit val colorTas = new Coloring[Tasmania, White.type ]




  new ValidlyColoredMapOfAustralia
}

