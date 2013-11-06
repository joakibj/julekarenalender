package com.github.julekarenalender.repository

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, ShouldMatchers, FlatSpec}
import java.io.File
import com.github.julekarenalender.Participant

class SQLiteParticipantRepositorySpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val repo = new SQLiteParticipantRepository

  before {
    repo.deleteAll()
  }

  "find" should "insert a participant and get it back by id" in {
    val id = repo.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))
    val participant = repo.find(id).get

    participant should be(Participant(Some(id), "Bjarne", "Bjarne.jpg", 0))
  }

  "find" should "not find any participant" in {
    val participant = repo.find(999)

    participant should be('empty)
  }

  "findAll" should "find all inserted participants" in {
    repo.insert(Participant(None, "Arne", "Arne.jpg", 0))
    repo.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))
    repo.insert(Participant(None, "Clara", "Clara.jpg", 0))

    val participants = repo.findAll()

    participants should have size (3)
  }

  "findAll" should "not find any participants" in {
    val participants = repo.findAll()

    participants should be('empty)
  }

  "insertAll" should "insert all participants" in {
    val toBeInserted = List(
      Participant(None, "Arne", "Arne.jpg", 0),
      Participant(None, "Bjarne", "Bjarne.jpg", 0),
      Participant(None, "Clara", "Clara.jpg", 0)
    )

    repo.insertAll(toBeInserted)
    val participants = repo.findAll()

    participants should have size (3)
  }

  "delete" should "delete an inserted participant" in {
    val id = repo.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))

    repo.delete(id)
    val participants = repo.findAll()

    participants should be ('empty)
  }

  "deleteAll" should "delete all inserted participants" in {
    val id = repo.insert(Participant(None, "Bjarne", "Bjarne.jpg", 0))

    repo.delete(id)
    val participants = repo.findAll()

    participants should be ('empty)
  }
}
