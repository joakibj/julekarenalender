package com.github.julekarenalender.view.util

import org.scalatest._
import java.lang.String

class ImagesSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  val Images = new Images with MockImageLocations

  it should "create a static image" in {
    Images.staticImg("Arne.jpg") should not be (Images.Blank)
  }

  it should "return a blank image for non-existing static image" in {
    Images.staticImg("Aesop.jpg") should be(Images.Blank)
  }

  it should "create a local image" in {
    Images.localImg("Arne.jpg") should not be (Images.Blank)
  }

  it should "create a blank image if a file does not exist" in {
    Images.localImg("Aesop.jpg") should be(Images.Blank)
  }

  private def getTestResourceImagePath: String = {
    getClass.getClassLoader.getResource(s"images").toURI.getPath
  }

  trait MockImageLocations extends ImageLocations {
    override def staticImageLocation = "images"

    override def localImageLocation = getTestResourceImagePath
  }

}
