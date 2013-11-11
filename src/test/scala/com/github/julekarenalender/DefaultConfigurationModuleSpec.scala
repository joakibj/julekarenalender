package com.github.julekarenalender

import org.scalatest._
import scala.util.Success
import com.github.julekarenalender.repository.{H2, DataAccessModule}

class DefaultConfigurationModuleSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val configModule = new DefaultConfigurationModule(new DataAccessModule(H2()))

  it should "get an empty list of participants" in {
    configModule.getParticipants should have size (0)
  }

  it should "get a list of participant after creating participants" in {
    val toBeCreated = List(
      Participant(None, "Arne", "Arne.jpg", 0),
      Participant(None, "Bjarne", "Bjarne.jpg", 0),
      Participant(None, "Clara", "Clara.jpg", 0)
    )
    configModule.createParticipants(toBeCreated) should be(Success(()))
    configModule.getParticipants should have size (3)
  }

}
