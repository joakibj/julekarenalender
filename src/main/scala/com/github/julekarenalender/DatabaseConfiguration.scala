package com.github.julekarenalender

import scala.slick.session.Database

trait DatabaseConfiguration {
  def database: Database
  protected def hasRunDdl: Boolean
  protected def runDdl(): Unit

  if(!hasRunDdl) {
    runDdl()
  }
}
