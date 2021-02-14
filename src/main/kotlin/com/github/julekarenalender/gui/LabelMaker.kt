package com.github.julekarenalender.gui

import no.jervell.view.awt.Anchor
import no.jervell.view.awt.Scaling
import no.jervell.view.awt.Label
import java.awt.Color
import java.awt.Font

class LabelMaker {

    private val fontName: String = "Times New Roman"
    private val fontSize: Int = 50
    private val plainFont: Font = Font(fontName, Font.PLAIN, fontSize)
    private val boldFont: Font = Font(fontName, Font.BOLD, fontSize)
    private val textColour: Color = Color.black

    fun createDateLabel(date: Int): Label {
        val label: Label = createLabel(date.toString())
        label.font = boldFont
        return label
    }

    fun createPersonLabel(name: String): Label {
        val label: Label = createLabel(name.toUpperCase())
        label.scaling = Scaling.STRETCH
        return label
    }

    private fun createLabel(text: String): Label {
        val label = Label(text)
        label.paint = textColour
        label.anchor = Anchor.CENTER
        label.font = plainFont
        return label
    }
}