package com.github.julekarenalender

import scala.util.Try

trait ConfigurationModule {
  def getParticipants: List[Participant]
  def syncParticipants(participants: List[Participant]): Try
}
