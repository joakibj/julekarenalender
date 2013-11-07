package com.github.julekarenalender

import scala.util.Try

class DefaultConfigurationModule extends ConfigurationModule {
  def getParticipants: List[Participant] = ???

  def syncParticipants(participants: List[Participant]): Try = ???
}
