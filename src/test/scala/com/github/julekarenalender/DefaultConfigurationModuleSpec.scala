package com.github.julekarenalender

import org.scalatest._
import scala.util.Success
import com.github.julekarenalender.repository.{H2, DataAccessModule}

class DefaultConfigurationModuleSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  var configModule = new DefaultConfigurationModule(new DataAccessModule(H2()))

  before {
    configModule.dataAccess.Participants.deleteAll()
  }

  it should "get an empty list of participants" in {
    configModule.getParticipants should have size (0)
  }

  it should "get a list of participant after creating participants" in {
    val toBeCreated = List(
      Participant(None, "Arne", "Arne.jpg", 0),
      Participant(None, "Bjarne", "Bjarne.jpg", 0),
      Participant(None, "Clara", "Clara.jpg", 0)
    )
    configModule.createParticipants(toBeCreated) should be(List(1,2,3))
    configModule.getParticipants should have size (3)
  }

  it should "sync participants" in {
    val toBeCreated = List(
      Participant(None, "Arne", "Arne.jpg", 0),
      Participant(None, "Bjarne", "Bjarne.jpg", 0),
      Participant(None, "Clara", "Clara.jpg", 0)
    )
    val created = configModule.createParticipants(toBeCreated)

    val toBeChanged = List(
      Participant(Some(created(0)), "Ole", "Ole.jpg", 1),
      Participant(Some(created(1)), "Dole", "Dole.jpg", 2),
      Participant(Some(created(2)), "Doffen", "Doffen.jpg", 3)
    )
    configModule.syncParticipants(toBeChanged) should be(Success(()))
    val updated = configModule.getParticipants
    updated should have size (3)

    updated(0) should be(Participant(Some(created(0)), "Ole", "Ole.jpg", 1))
    updated(1) should be(Participant(Some(created(1)), "Dole", "Dole.jpg", 2))
    updated(2) should be(Participant(Some(created(2)), "Doffen", "Doffen.jpg", 3))
  }

}
