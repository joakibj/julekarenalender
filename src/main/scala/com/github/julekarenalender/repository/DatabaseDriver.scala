package com.github.julekarenalender.repository

import java.io.File
import scala.slick.driver.{H2Driver, SQLiteDriver, ExtendedProfile}

trait DatabaseType

object InMemory extends DatabaseType

object InFile extends DatabaseType

trait DatabaseDriver {
  val profile: ExtendedProfile

  def connUrl: String

  def inFileConnUrl: String

  def inMemConnUrl: String

  def driverName: String
}

abstract class ADatabaseDriver(dbType: DatabaseType) extends DatabaseDriver {
  protected[this] def curDir = new File(".").getAbsolutePath.dropRight(1)

  protected[this] def sep = File.separator

  override def connUrl: String = dbType match {
    case InMemory => inMemConnUrl
    case InFile => inFileConnUrl
  }
}

case class SQLite(dbType: DatabaseType = InFile) extends ADatabaseDriver(dbType) {

  def inFileConnUrl: String = "jdbc:sqlite:%s%sdata.db" format(curDir, sep)

  def inMemConnUrl: String = "jdbc:sqlite:file::memory:?cache=shared"

  def driverName: String = "org.sqlite.JDBC"

  val profile: ExtendedProfile = SQLiteDriver
}

case class H2(dbType: DatabaseType = InMemory) extends ADatabaseDriver(dbType) {

  def inFileConnUrl: String = "jdbc:h2:file:%s/data" format (curDir)

  def inMemConnUrl: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

  def driverName: String = "org.h2.Driver"

  val profile: ExtendedProfile = H2Driver
}
