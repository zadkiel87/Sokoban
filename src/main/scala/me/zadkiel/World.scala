package me.zadkiel

import scala.collection.mutable.ListBuffer
import scala.io.Source
import me.zadkiel.element._
import swing.event.Key._
import scala.Array
import swing.Publisher
import swing.event.Event

case class PlayerMove() extends Event

object World extends Publisher{
  type Level = Array[Array[WorldElement]]
  val levels = ListBuffer[Level]()

  val MAX_WIDTH = 19
  val MAX_HEIGHT = 16

  var currentLevel: Level = _

  var manX = 0
  var manY = 0

  var toGoX = 0
  var toGoY = 0

  var nb_move = 0

  def loadLevel(num: Int) {
    currentLevel = copyMap(levels(num))
    nb_move = 0
    publish(PlayerMove())
  }

  def onKeyPress(keyCode: Value) {
    keyCode match {
      case Left => move(-1, 0)
      case Right => move(1, 0)
      case Up => move(0, 1)
      case Down => move(0, -1)
      case _ =>
    }
  }

  def fromString(stringWorld: String): Level = {
    val level = Array.ofDim[WorldElement](MAX_WIDTH, MAX_HEIGHT)
    val listStringMap = stringWorld.toList

    var wEl: WorldElement = null
    var x, y = 0
    for (i <- 0 until listStringMap.length) {
      listStringMap(i) match {
        case '#' => wEl = Wall()
        case '.' => wEl = Storage()
        case 'o' => wEl = Crate()
        case '@' => wEl = Man()
        case '+' => wEl = ManOnStorage()
        case '*' => wEl = ManOnStorage()
        case ' ' => wEl = element.Space()
        case '\n' =>
          y += 1
          x = 0
        case _ =>

      }
      if (wEl != null) {
        level(x)(y) = wEl
        x += 1
        wEl = null
      }
    }
    level
  }

  def fromFile(filePath: String) {
    var lines: ListBuffer[String] = ListBuffer()
    val stream = Source.fromInputStream(getClass.getResourceAsStream(filePath))
    for ((line) <- stream.getLines()) {
      line match {
        case "" =>
          levels += fromString(lines.mkString("\n"))
          lines = ListBuffer()
        case _ => lines += line
      }
    }
  }

  private def locateMan() {
    for {i <- 0 until currentLevel.length
         j <- 0 until currentLevel(i).length} {
      if (currentLevel(i)(j).isInstanceOf[Man]) {
        manX = i
        manY = j
        return
      }
    }
  }

  private def move(x: Int, y: Int) {
    locateMan()
    toGoX = x
    toGoY = y
    nb_move += 1
    currentLevel(manX + toGoX)(manY - toGoY) match {
      case e: Crate => moveCrate()
      case e: Space => moveManToSpace()
      case e: Storage => moveManToStorage()
      case e: CrateOnStorage => moveCrate()
      case _ => nb_move -= 1
    }
    publish(PlayerMove())
  }

  private def moveCrate() {
    val (x, y) = nextCase(manX, manY)
    val (bhCrateX, bhCrateY) = nextCase(x, y)
    currentLevel(bhCrateX)(bhCrateY) match {
      case e: Space =>
        currentLevel(bhCrateX)(bhCrateY) = Crate()
        moveManToSpaceOrStorage(x, y)
      case e: Storage =>
        currentLevel(bhCrateX)(bhCrateY) = CrateOnStorage()
        moveManToSpaceOrStorage(x, y)
      case unknow =>
    }
  }

  private def moveManToSpace() {
    letSpaceOrStorage()
    manX = manX + toGoX
    manY = manY - toGoY
    currentLevel(manX)(manY) = Man()
  }

  private def moveManToStorage() {
    letSpaceOrStorage()
    manX = manX + toGoX
    manY = manY - toGoY
    currentLevel(manX)(manY) = ManOnStorage()

  }

  private def letSpaceOrStorage() {
    currentLevel(manX)(manY) match {
      case e: Man =>
        currentLevel(manX)(manY) = element.Space()
      case e: ManOnStorage =>
        currentLevel(manX)(manY) = Storage()
    }
  }

  private def moveManToSpaceOrStorage(x: Int, y: Int) {
    currentLevel(x)(y) match {
      case e: Crate =>
        moveManToSpace()
      case e: CrateOnStorage =>
        moveManToStorage()
    }
  }

  private def nextCase(x: Int, y: Int) = {
    (x + toGoX, y - toGoY)
  }

  private def copyMap(originMap: Level):Level = {
    val copyMap = Array.ofDim[WorldElement](MAX_WIDTH, MAX_HEIGHT)
    for {x <- 0 until originMap.length
         y <- 0 until originMap(x).length}
      copyMap(x)(y) = originMap(x)(y)
    copyMap
  }
}