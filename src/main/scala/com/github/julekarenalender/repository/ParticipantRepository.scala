package com.github.julekarenalender.repository

import com.github.julekarenalender.Participant

trait ParticipantRepository extends UsingDatabaseConnection {

  import driver.profile.simple._

  object Participants extends Table[Participant]("PARTICIPANTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME", O.NotNull)

    def image = column[String]("IMAGE_NAME", O.NotNull)

    def win = column[Int]("WIN")

    def * = id.? ~ name ~ image ~ win <>(Participant, Participant.unapply _)

    def forInsert = name ~ image ~ win <>( {
      t => Participant(None, t._1, t._2, t._3)
    }, {
      (p: Participant) => Some((p.name, p.image, p.daysWon))
    })

    def find(id: Int): Option[Participant] = database withSession {
      implicit session: Session =>
        Query(Participants).where(_.id === id).firstOption
    }

    def findAll(): List[Participant] = database withSession {
      implicit session: Session =>
        Query(Participants).list()
    }

    def insert(t: Participant): Int = database withSession {
      implicit session: Session =>
        Participants.forInsert returning Participants.id insert (t)
    }

    def insertAll(lt: List[Participant]): List[Int] = {
      database withSession {
        implicit session: Session =>
          for {
            p <- lt
          } yield Participants.insert(p)
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


    def update(t: Participant) {
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

}
