package com.github.julekarenalender.repository

import scala.slick.driver.SQLiteDriver.simple._

class SQLiteParticipantRepository extends ParticipantRepository with SQLiteDatabaseConfiguration {

  def find(id: Int): Option[T] = database withSession {
    implicit session: Session =>
      Query(Participants).where(_.id === id).firstOption
  }

  def findAll(): List[T] = database withSession {
    implicit session: Session =>
      Query(Participants).list()
  }

  def insert(t: T): Int = database withSession {
    implicit session: Session =>
      Participants.forInsert returning Participants.id insert (t)
  }

  def insertAll(lt: List[T]): Unit = {
    database withSession {
      implicit session: Session =>
        lt.foreach {
          t =>
            Participants.insert(t)
        }
    }
  }

  def delete(id: Int) {
    database withSession {
      implicit session: Session =>
        Query(Participants).where(_.id === id).delete
    }
  }

  def deleteAll() {
    database withSession {
      implicit session: Session =>
        Query(Participants).delete
    }
  }

  def update(t: T) {
    database withSession {
      implicit session: Session =>
        val query = for {
          p <- Participants
          if p.id === t.id
        } yield p
        query.update(t)
    }
  }
}
