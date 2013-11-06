package com.github.julekarenalender

import scala.slick.driver.SQLiteDriver.simple._
import java.io.File

class SQLiteParticipantRepository extends ParticipantRepository with SQLiteDatabaseConfiguration {

  def find(id: Int): Option[T] = database withSession {
    implicit session: Session =>
      Query(Participants).filter(_.id === id).firstOption match {
        case Some((_id, name, image, win)) =>
          Some(Participant(_id, name, new File(image), win))
        case _ =>
          None
      }
  }

  def findAll(): List[T] = ???

  def insert(t: T): Unit = database withSession {
    implicit session: Session =>
      Participants.insert(t.id, t.name, t.image.getName, t.win)
  }

  def insertAll(lt: List[T]): Unit = ???

}
