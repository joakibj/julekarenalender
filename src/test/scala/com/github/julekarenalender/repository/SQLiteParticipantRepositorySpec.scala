package com.github.julekarenalender.repository

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, ShouldMatchers, FlatSpec}
import java.io.File
import com.github.julekarenalender.Participant
import scala.slick.driver.SQLiteDriver

class SQLiteParticipantRepositorySpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val repo = new DataAccessModule(H2())

  before {
    repo.Participants.deleteAll()
  }

  "find" should "insert a participant and get it back by id" in {
    val id = repo.Participants.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))
    val participant = repo.Participants.find(id).get

    participant should be(Participant(Some(id), "Bjarne", "Bjarne.jpg", 0))
  }

  "find" should "not find any participant" in {
    val participant = repo.Participants.find(999)

    participant should be('empty)
  }

  "findAll" should "find all inserted participants" in {
    repo.Participants.insert(Participant(None, "Arne", "Arne.jpg", 0))
    repo.Participants.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))
    repo.Participants.insert(Participant(None, "Clara", "Clara.jpg", 0))

    val participants = repo.Participants.findAll()

    participants should have size (3)
  }

  "findAll" should "not find any participants" in {
    val participants = repo.Participants.findAll()

    participants should be('empty)
  }

  "insertAll" should "insert all participants" in {
    val toBeInserted = List(
      Participant(None, "Arne", "Arne.jpg", 0),
      Participant(None, "Bjarne", "Bjarne.jpg", 0),
      Participant(None, "Clara", "Clara.jpg", 0)
    )

    repo.Participants.insertAll(toBeInserted)

    val participants = repo.Participants.findAll()
    participants should have size (3)
  }

  "delete" should "delete an inserted participant" in {
    val id = repo.Participants.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))

    repo.Participants.delete(id)

    val participants = repo.Participants.findAll()
    participants should be('empty)
  }

  "deleteAll" should "delete all inserted participants" in {
    val id = repo.Participants.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))

    repo.Participants.delete(id)

    val participants = repo.Participants.findAll()
    participants should be('empty)
  }

  "update" should "update an inserted participant" in {
    val id = repo.Participants.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))

    repo.Participants.update(Participant(Some(id), "Arne", "Arne.jpg", 0))

    val participant = repo.Participants.find(id).get
    participant should be(Participant(Some(id), "Arne", "Arne.jpg", 0))
  }

}
