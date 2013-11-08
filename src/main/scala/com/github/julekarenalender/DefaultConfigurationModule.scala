package com.github.julekarenalender

import scala.collection.JavaConverters._
import scala.util.{Success, Try}
import java.io.File
import no.jervell.util.SimpleLogger
import no.jervell.repository.impl.{DefaultPersonDAO, CSVFile}
import no.jervell.domain.Person

class DefaultConfigurationModule extends ConfigurationModule {

  def getParticipants: List[Participant] = {
    dataAccess.Participants.findAll()
  }

  def getParticipantsJava: java.util.Collection[Participant] = {
    dataAccess.Participants.findAll().asJavaCollection
  }

  def syncParticipantsJava(participants: java.util.List[Participant]): Try[Unit] = {
    val scalaParticipants = participants.asScala
    Try(
      for {
        p <- scalaParticipants
      } yield dataAccess.Participants.update(p)
    )
  }

  def syncParticipants(participants: List[Participant]): Try[Unit] = {
    println(participants)
    Try(participants.foreach {
      p =>
        dataAccess.Participants.update(p)
    })
  }

  def scanParticipants: List[Participant] = {
    val resourceFile: File = new File(".", "julekarenalender.csv")
    SimpleLogger.getInstance.info("Loading configuration from: " + resourceFile)
    val dataSource: CSVFile = new CSVFile(resourceFile, true)
    val persons = new DefaultPersonDAO(dataSource).getPersonList.asScala.toList

    val participants =
      for {
        p: Person <- persons
      } yield Participant(None, p.getName, p.getPicture, p.getDay)
    participants
  }

  def createParticipants(participants: List[Participant]): Try[Unit] = {
    Try(dataAccess.Participants.insertAll(participants))
  }

  def importParticipantsFromCsv(): Try[Unit] = {
    val participants = scanParticipants
    if(participants.map(_.name) != getParticipants.map(_.name)) {
      createParticipants(participants)
    }
    Success()
  }
}
