package com.github.julekarenalender.config

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import java.io.File
import no.jervell.util.SimpleLogger
import com.github.julekarenalender.repository.{SQLite, DataAccessModule}
import com.github.julekarenalender.Participant

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
    Try(participants.foreach {
      p =>
        dataAccess.Participants.update(p)
    })
  }

  def scanParticipants: List[Participant] = {
    SimpleLogger.getInstance.info("Scanning images/ and importing participants...")

    val participants =
      for {
        f: File <- participantImages
      } yield Participant(None, f.getName.split('.').dropRight(1).head, f.getName, 0)

    participants
  }

  private def participantImages: List[File] = {
    val participantImageFilter: (File) => Boolean = (f) => {
      f.getName.split('.').drop(1).lastOption match {
        case Some("png") | Some("jpg") => !f.getName.contains("bonus")
        case None => false
        case _ => false
      }
    }

    Try(new File(".", "images").listFiles().toList.filter(participantImageFilter)) match {
      case Success(li) => li
      case Failure(ex) =>
        SimpleLogger.getInstance().error("Unable to find the folder ./images")
        Nil
    }
  }

  def createParticipants(participants: List[Participant]): List[Int] = {
    SimpleLogger.getInstance.info(s"Creating ${participants.size} participants.")
    dataAccess.Participants.insertAll(participants)
  }

  def importParticipants(): Try[Unit] = {
    val participants = scanParticipants
    val persistedParticipants = getParticipants
    val nameDiff = participants.filter(p => !persistedParticipants.map(_.name).contains(p.name))
    if (nameDiff.size > 0) {
      createParticipants(nameDiff)
    }
    Success()
  }
}