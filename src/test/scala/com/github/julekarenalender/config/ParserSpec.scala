package com.github.julekarenalender.config

import org.scalatest._
import java.util.Calendar
import scala.collection.JavaConversions._

class ParserSpec extends FlatSpec with ShouldMatchers {
  it should "return a list of todays day if days is empty" in {
    val actual = Parser.toDays(List())

    actual should have size(1)
    actual.get(0) should be(new Integer(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))
  }

  it should "return a list of days when supplied" in {
    val actual = Parser.toDays(List("1", "3", "5"))

    actual should be(seqAsJavaList(List(new Integer(1), new Integer(3), new Integer(5))))
  }

  it should "return a sorted list of days when unsorted list is supplied" in {
    val actual = Parser.toDays(List("3", "1", "5"))

    actual should be(seqAsJavaList(List(new Integer(1), new Integer(3), new Integer(5))))
  }
}
