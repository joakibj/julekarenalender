package com.github.julekarenalender

import scala.collection.JavaConverters._
import scala.util.{Success, Try}
import java.io.File
import no.jervell.util.SimpleLogger
import com.github.julekarenalender.repository.{SQLite, DataAccessModule}

class DefaultConfigurationModule(override val dataAccess: DataAccessModule = new DataAccessModule(SQLite())) extends ConfigurationModule {

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
    val participantImageFilter: (File) => Boolean = (f) => {
      f.getName.split('.').drop(1).lastOption match {
        case Some("png") | Some("jpg") => true
        case None => false
        case _ => false
      }
    }

    SimpleLogger.getInstance.info("Scanning images/ and importing participants.")

    val participantImages = new File(".", "images").listFiles().toList.filter(participantImageFilter)

    val participants =
      for {
        f: File <- participantImages
      } yield Participant(None, f.getName.split('.').dropRight(1).head, f.getName, 0)

    participants
  }

  def createParticipants(participants: List[Participant]): Try[Unit] = {
    SimpleLogger.getInstance.info(s"Creating ${participants.size} participants.")
    Try(dataAccess.Participants.insertAll(participants))
  }

  def importParticipants(): Try[Unit] = {
    val participants = scanParticipants
    if (participants.map(_.name) != getParticipants.map(_.name)) {
      createParticipants(participants)
    }
    Success()
  }
}
