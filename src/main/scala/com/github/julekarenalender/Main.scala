package com.github.julekarenalender

import no.jervell.view.MainWindow
import com.github.julekarenalender.config.{Parser, DefaultConfigurationModule, AppInfo}
import com.github.julekarenalender.log.Logging

case class Config(days: Seq[String] = Seq(), debug: Boolean = false, scan: Boolean = false, bonus: Boolean = false, reset: Boolean = false)

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
    opt[Unit]("bonus") optional() action {
      (_, c) =>
        c.copy(bonus = true)
    } text("Enables the bonus wheel")
    opt[Unit]("debug") optional() action {
      (_, c) =>
        c.copy(debug = true)
    } text("Turns on debug mode")
    opt[Unit]("reset") optional() action {
      (_, c) =>
        c.copy(reset = true)
    } text("Resets all configuration. Use at own risk!")
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
    new MainWindow(Parser.toDays(config.days), new DefaultConfigurationModule(config)).display()
  }
}
