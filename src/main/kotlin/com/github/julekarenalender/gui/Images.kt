package com.github.julekarenalender.gui


import com.github.julekarenalender.logger
import no.jervell.view.awt.Anchor
import no.jervell.view.awt.Image
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URI

class Images {
    private val staticImageLocation = "static/images"
    private val localImageLocation = "images"

    fun getBonusImages(): List<Image> {
        val maybeBonusFiles = File(localImageLocation).listFiles() ?: return listOf()
        return maybeBonusFiles.asList()
                .filter { it.name.contains("bonus") }
                .map { loadImage(it.toURI()) }
    }

    fun getLocalImg(name: String): Image {
        return loadImage(File(localImageLocation, name).toURI())
    }

    fun getStaticImg(name: String): Image {
        val staticFilename = "$staticImageLocation/$name"
        val maybeFile = javaClass.classLoader.getResource(staticFilename)

        if (maybeFile == null) {
            logger.warn("Could not find image: $staticFilename")
            return blankImage()
        }

        return loadImage(maybeFile.toURI())
    }

    private fun blankImage(): Image {
        val width = 10
        val height = 10
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
        val gfx: Graphics = bufferedImage.graphics
        gfx.color = Color.WHITE
        gfx.fillRect(0, 0, width, height)
        return Image(bufferedImage)
    }

    private fun loadImage(uri: URI): Image {
        try {
            val img = Image(uri)
            img.anchor = Anchor.CENTER
            return img
        } catch (e: IOException) {
            logger.warn("Unable to read file: $uri")
        }
        return blankImage()
    }

}