package com.github.julekarenalender.view.awt.util

import no.jervell.view.awt.{Anchor, Scaling, Label}
import java.lang.String
import scala.Predef.String
import java.awt.{Color, Font}

object LabelPrinter {
  def apply() = new DefaultLabelMaker
}

class DefaultLabelMaker extends LabelMaker {
  private final val FontName: String = "Times New Roman"
  private final val FontSize: Int = 50
  private final val PlainFont: Font = new Font(FontName, Font.PLAIN, FontSize)
  private final val BoldFont: Font = new Font(FontName, Font.BOLD, FontSize)
  private final val TextColour: Color = Color.black

  def createDateLabel(date: Int): Label = {
    val label: Label = createLabel(String.valueOf(date))
    label.setFont(BoldFont)
    label
  }

  def createPersonLabel(name: String): Label = {
    val label: Label = createLabel(name.toUpperCase)
    label.setScaling(Scaling.STRETCH)
    label
  }

  def createLabel(text: String): Label = {
    val label: Label = new Label(text)
    label.setPaint(TextColour)
    label.setAnchor(Anchor.CENTER)
    label.setFont(PlainFont)
    label
  }
}

trait LabelMaker {
  def createPersonLabel(name: String): Label
  def createDateLabel(dateValue: Int): Label
  def createLabel(text: String): Label
}
