package com.github.julekarenalender

import no.jervell.view.MainWindow
import com.github.julekarenalender.config.{Parser, DefaultConfigurationModule, AppInfo}
import com.github.julekarenalender.log.Logging

case class Config(days: Seq[String] = Seq(), debug: Boolean = false, scan: Boolean = false)

object Main extends App with Logging {
  val parser = new scopt.OptionParser[Config]("julekarenalender") {
    head(AppInfo.ProgramName, AppInfo.Version)
    arg[String]("days") unbounded() optional() action {
      (x, c) =>
        c.copy(days = c.days :+ x)
    } text("List of days there should be a draw. Optional")
    opt[Unit]("scan") optional() action {
      (_, c) =>
        c.copy(scan = true)
    } text("Scans the images/ folder for participants")
    opt[Unit]("debug") optional() action {
      (_, c) =>
        c.copy(debug = true)
    } text("Turns on debug mode")
    help("help") text ("prints this usage text")
  }

  parser.parse(args, Config()) map {
    config =>
      if(config.debug) logger.enableDebug()
      runMainWindow(config)
  } getOrElse {
    logger.error("Unable to parse arguments")
  }

  private def runMainWindow(config: Config) {
    new MainWindow(Parser.toDays(config.days), initConfigurationModule(config)).display()
  }

  private def initConfigurationModule(config: Config) = {
    val configModule = new DefaultConfigurationModule
    if(config.scan) configModule.importParticipants()
    configModule
  }
}
