package com.github.julekarenalender.repository

import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.session.Database
import scala.slick.jdbc.meta.MTable
import com.github.julekarenalender.Participant

trait SQLiteDatabaseConfiguration extends DatabaseConfiguration with FileDatabase {

  def database = Database.forURL(connUrl, driver = "org.sqlite.JDBC")

  override def runDdl {
    database withSession {
      implicit session: Session =>
        (Participants.ddl).create
    }
  }

  override def hasRunDdl: Boolean = {
    database withSession {
      implicit session: Session =>
        val tableNames = MTable.getTables.list().map(_.name.name).toSet
        tableNames.contains(Participants.tableName)
    }
  }

  object Participants extends Table[Participant]("PARTICIPANTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME", O.NotNull)

    def image = column[String]("IMAGE_LOCATION", O.NotNull)

    def win = column[Int]("WIN")

    def * = id.? ~ name ~ image ~ win <>(Participant, Participant.unapply _)

    def forInsert = name ~ image ~ win <>( {
      t => Participant(None, t._1, t._2, t._3)
    }, {
      (p: Participant) => Some((p.name, p.image, p.win))
    })
  }

}
