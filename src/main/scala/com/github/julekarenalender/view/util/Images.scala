package com.github.julekarenalender.view.util

import scala.collection.JavaConverters._
import no.jervell.view.awt.{Anchor, Image}
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics}
import com.github.julekarenalender.log.Logging
import java.net.URI
import java.io.File
import scala.util.{Failure, Success, Try}

object Images {
  def apply() = new Images with DefaultImageLocations
}

class Images extends Logging {
  this: ImageLocations =>
  private val BlankColour = Color.WHITE
  val Blank: Image = blankImage

  def staticImg(name: String): Image = {
    val staticFilename = s"$staticImageLocation/$name"
    Try(getClass.getClassLoader.getResource(staticFilename).toURI) match {
      case Success(uri) => image(new File(uri))
      case Failure(ex) => {
        logger.error(s"Unable to load static image: $staticFilename")
        Blank
      }
    }
  }

  def localImg(name: String): Image = {
    image(new File(localImageLocation, name))
  }

  def bonusImages(): java.util.List[Image] = {
    val files = getBonusFiles
    files.map(f => image(f)).asJava
  }

  def image(file: File): Image = {
    loadGeneric(file.toURI) match {
      case Success(image) => image
      case Failure(ex) => {
        logger.error(s"Unable to load ${file.getAbsolutePath}")
        Blank
      }
    }
  }

  private def loadGeneric(uri: URI): Try[Image] = {
    Try {
      val image: Image = new Image(uri)
      image.setAnchor(Anchor.CENTER)
      image
    }
  }

  private def blankImage: Image = {
    val width: Int = 10
    val height: Int = 10
    val bufferedImage: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
    val gfx: Graphics = bufferedImage.getGraphics
    gfx.setColor(BlankColour)
    gfx.fillRect(0, 0, width, height)
    new Image(bufferedImage)
  }

  private def getBonusFiles: List[File] = {
    val imageFiles = Try(new File(localImageLocation).listFiles)
    imageFiles match {
      case Success(bf) => bf.filter(_.getName.contains("bonus")).toList
      case Failure(ex) => {
        logger.error("Unable to list files in ./images", ex)
        Nil
      }
    }
  }
}

trait DefaultImageLocations extends ImageLocations {
  override def staticImageLocation = "static/images"

  override def localImageLocation = "./images"
}

trait ImageLocations {
  def staticImageLocation: String

  def localImageLocation: String
}