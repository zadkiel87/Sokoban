package me.zadkiel.ui

import me.zadkiel.World
import me.zadkiel.element._
import swing.Swing._
import swing.Panel
import swing.event._
import java.awt.{Color, Graphics2D}

case class LevelFinish() extends Event

class UIWorld() extends Panel {
  var currentLevelNum: Int = _

  background = Color.white
  preferredSize = (500, 400)
  focusable = true
  listenTo(keys)
  reactions += {
    case KeyTyped(_, 't', _, _) =>
      publish(LevelFinish())
    case KeyTyped(_, 'r', _, _) =>
      restart()
    case KeyPressed(_, key, _, _) =>
      World.onKeyPress(key)
      repaint()
    case _: FocusLost => repaint()
  }
  var success = true

  def loadWorld(levelNum: Int) {
    currentLevelNum = levelNum
    World.loadLevel(levelNum)
    repaint()
    requestFocus()
  }

  def nextLevel() {
    loadWorld(currentLevelNum + 1)
  }

  def restart() {
    World.loadLevel(currentLevelNum)
    repaint()
  }

  override def paintComponent(g: Graphics2D) {
    g.clearRect(0, 0, size.width, size.height)
    val level = World.currentLevel
    for (x <- 0 until level.length) {
      for (y <- 0 until level(x).length if level(x)(y) != null) {
        g.drawString(WorldElement.representation(level(x)(y)), (x + 1) * 20, (y + 1) * 20);
        level(x)(y) match {
          case Crate() =>
            success = false
          case _ =>
        }
      }
    }
    if (success) {
      publish(LevelFinish())
    } else {
      success = true
    }
  }
}