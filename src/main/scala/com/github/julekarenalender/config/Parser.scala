package com.github.julekarenalender.config

import scala.collection.JavaConversions._
import java.util.Calendar

object Parser {
  def toDays(days: Seq[String]): java.util.List[Integer] = {
    if(days.size == 0) seqAsJavaList(List(Calendar.getInstance.get(Calendar.DAY_OF_MONTH)))
    else seqAsJavaList(days.map(i => new Integer(i)).sorted)
  }
}
