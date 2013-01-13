package me.zadkiel.element
trait WorldElement

case class Crate() extends WorldElement
case class CrateOnStorage() extends WorldElement
case class Man() extends WorldElement
case class ManOnStorage() extends WorldElement
case class Space() extends WorldElement
case class Storage() extends WorldElement
case class Wall() extends WorldElement

object WorldElement {
  def representation(we: WorldElement): String = we match {
    case Crate() => "o"
    case CrateOnStorage() => "*"
    case Man() => "@"
    case ManOnStorage() => "+"
    case Space() => ""
    case Storage() => "."
    case Wall() => "#"
  }
}