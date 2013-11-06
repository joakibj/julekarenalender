package com.github.julekarenalender.repository

import java.io.File

trait InMemoryDatabase extends DatabaseType {
  def connUrl = "jdbc:sqlite:file::memory:?cache=shared"
}

trait FileDatabase extends DatabaseType {
  private def curDir = new File(".").getAbsolutePath
  private def sep = File.separator

  def connUrl = "jdbc:sqlite:%s%sdata.db" format(curDir.dropRight(1), sep)
}

trait DatabaseType {
  def connUrl: String
}
