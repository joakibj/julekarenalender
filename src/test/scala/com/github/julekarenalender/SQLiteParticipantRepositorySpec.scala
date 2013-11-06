package com.github.julekarenalender

import org.scalatest.{ShouldMatchers, FlatSpec}
import java.io.File

class SQLiteParticipantRepositorySpec extends FlatSpec with ShouldMatchers {
  val repo = new SQLiteParticipantRepository

  "find" should "get a participant by id" in {
    //repo.insert(Participant(0, "Bjarne", new File("Bjarne.jpg"), 0))
    val p = repo.find(0) should not be empty
    p should be(Participant(0, "Bjarne", new File("Bjarne.jpg"), 0))
  }
}
