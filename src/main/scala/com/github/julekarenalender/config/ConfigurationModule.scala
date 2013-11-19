package com.github.julekarenalender.config

import scala.util.Try
import com.github.julekarenalender.repository.{SQLite, DataAccessModule}
import com.github.julekarenalender.{Config, Participant}

trait ConfigurationModule extends JavaCompatibility {
  protected[this] val dataAccess: DataAccessModule

  def config: Config

  def getParticipants: List[Participant]

  def syncParticipants(participants: List[Participant]): Try[Unit]

  def createParticipants(participants: List[Participant]): List[Int]

  def importParticipants(): Try[Unit]

  protected[this] def scanParticipants: List[Participant]
}

trait JavaCompatibility {
  def getParticipantsJava: java.util.Collection[Participant]

  def syncParticipantsJava(participants: java.util.List[Participant]): Try[Unit]
}