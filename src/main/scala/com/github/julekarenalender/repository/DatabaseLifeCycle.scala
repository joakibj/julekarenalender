package com.github.julekarenalender.repository


trait DatabaseLifeCycle {
  protected def hasRunDdl: Boolean
  protected def runDdl(): Unit

  if(!hasRunDdl) {
    runDdl()
  }
}
