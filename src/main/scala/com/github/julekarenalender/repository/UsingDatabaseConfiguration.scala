package com.github.julekarenalender.repository

import scala.slick.jdbc.meta.MTable


trait UsingDatabaseConfiguration extends ParticipantRepository with DatabaseLifeCycle with UsingDatabaseConnection {
  this: UsingDatabaseDriver =>
  import driver.profile.simple._

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

}
