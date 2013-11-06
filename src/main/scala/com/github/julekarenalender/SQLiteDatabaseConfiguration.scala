package com.github.julekarenalender

import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.session.Database
import java.io.File
import scala.slick.jdbc.meta.MTable

trait SQLiteDatabaseConfiguration extends DatabaseConfiguration with FileDatabase {

  def database = Database.forURL(connUrl, driver = "org.sqlite.JDBC")

  def runDdl {
    database withSession {
      implicit session: Session =>
        (Participants.ddl).create
    }
  }

  def hasRunDdl: Boolean = {
    database withSession {
      implicit session: Session =>
        val tableNames = MTable.getTables.list().map(_.name.name).toSet
      println(Query(Participants).list())
        tableNames.contains(Participants.tableName)
    }
  }

  object Participants extends Table[(Int, String, String, Int)]("PARTICIPANTS") {
    def id = column[Int]("PARTICIPANT_ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def image = column[String]("IMAGE_LOCATION")

    def win = column[Int]("WIN")

    def * = id ~ name ~ image ~ win
  }

}
