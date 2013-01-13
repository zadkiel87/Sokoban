package me.zadkiel

import swing._
import ui.{LevelFinish, UIWorld}
import swing.ListView._
import swing.event._
import javax.swing.UIManager
import swing.BorderPanel.Position

object Sokoban extends SimpleSwingApplication {
  activeNimbus()

  World.fromFile("worlds.txt")

  val uiWorld = new UIWorld

  val uiChooseLevel = new FlowPanel {
    val comboBox = new ComboBox(World.levels) {
      renderer = Renderer(el => World.levels.indexOf(el))
      focusable = false
    }
    val loadLevelButton = new Button {
      text = "Load level"
      focusable = false
    }

    val restartButton = new Button {
      text = "Restart"
      focusable = false
    }

    contents += new Label("Change level")
    contents += comboBox
    contents += loadLevelButton
    contents += new Separator()
    contents += restartButton

    listenTo(loadLevelButton, restartButton)

    reactions += {
      case ButtonClicked(`loadLevelButton`) =>
        val levelNum = World.levels.indexOf(comboBox.selection.item)
        uiWorld.loadWorld(levelNum)
      case ButtonClicked(`restartButton`) =>
        uiWorld.restart()
    }
  }

  val nbMoveLabel = new Label() {
    listenTo(World)
    reactions += {
      case PlayerMove() => {
        text = "NB Move: " + World.nb_move
      }
    }
  }

  val uiStats = new FlowPanel {
    nbMoveLabel.text = "NB Move:"
    contents += nbMoveLabel
  }

  val borderPanel = new BorderPanel {
    add(uiChooseLevel, Position.North)
    add(uiWorld, Position.Center)
    add(uiStats, Position.South)

    uiWorld.loadWorld(0)

    listenTo(uiWorld)

    reactions += {
      case LevelFinish() =>
        val result = Dialog.showOptions(this,
          message = "Success, level finish with " + World.nb_move + " move.",
          title = "Level Finished!",
          messageType = Dialog.Message.Question,
          optionType = Dialog.Options.YesNo,
          entries = Seq("Next level", "Ok"),
          initial = 1)
        result match {
          case Dialog.Result.Yes  =>
            uiWorld.nextLevel()
          case _ =>
        }
    }
  }

  def top = new MainFrame {
    title = "Sokoban Game"
    contents = borderPanel
    centerOnScreen()
  }

  def activeNimbus() {
    for (info <- UIManager.getInstalledLookAndFeels()){
      if ("Nimbus".equals(info.getName())) {
        UIManager.setLookAndFeel(info.getClassName())
        return
      }
    }
  }
}
