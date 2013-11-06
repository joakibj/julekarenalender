package com.github.julekarenalender.repository

import scala.slick.session.Database

trait DatabaseConfiguration {
  def database: Database
  protected def hasRunDdl: Boolean
  protected def runDdl(): Unit

  if(!hasRunDdl) {
    runDdl()
  }
}
