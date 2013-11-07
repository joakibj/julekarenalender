package com.github.julekarenalender.repository

import java.io.File

trait InMemoryDatabase extends DatabaseType {
  override def connUrl = "jdbc:sqlite:file::memory:?cache=shared"
}

trait FileDatabase extends DatabaseType {
  private def curDir = new File(".").getAbsolutePath
  private def sep = File.separator

  override def connUrl = "jdbc:sqlite:%s%sdata.db" format(curDir.dropRight(1), sep)
}

trait DatabaseType {
  def connUrl: String
}
