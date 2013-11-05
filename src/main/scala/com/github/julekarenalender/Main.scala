package com.github.julekarenalender

import scala.collection.JavaConversions._
import no.jervell.util.SimpleLogger
import no.jervell.view.MainWindow
import no.jervell.jul.DayParser
import java.io.File
import no.jervell.repository.impl.{DefaultPersonDAO, CSVFile}

case class Config(days: Seq[String] = Seq(), debug: Boolean = false)

object Main extends App {

  val parser = new scopt.OptionParser[Config]("julekarenalender") {
    head("Julekarenalender", "2.0.0")
    arg[String]("days") unbounded() optional() action {
      (x, c) =>
        c.copy(days = c.days :+ x)
    } text("List of days there should be a draw. Optional")
    opt[Unit]("debug") optional() action {
      (_, c) =>
        c.copy(debug = true)
    } text("Turns on debug mode")
    help("help") text ("prints this usage text")
  }

  parser.parse(args, Config()) map {
    config =>
      SimpleLogger.getInstance.setDebug(config.debug)
      SimpleLogger.getInstance.setInfo(true)
      runMainWindow(config)
  } getOrElse {
    SimpleLogger.getInstance().error("Unable to parse arguments")
  }

  private def runMainWindow(config: Config) {
    new MainWindow(parseDays(config), loadDataSource()).display()
  }

  private def parseDays(config: Config): Array[Int] = {
    val dayParser: DayParser = new DayParser(config.days.map(_.toString))
    return dayParser.parse
  }

  private def loadDataSource() = {
    val resourceFile: File = new File(".", "julekarenalender.csv")
    SimpleLogger.getInstance.info("Loading configuration from: " + resourceFile)
    val dataSource: CSVFile = new CSVFile(resourceFile, true)
    new DefaultPersonDAO(dataSource)
  }
}