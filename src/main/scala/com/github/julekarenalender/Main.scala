package com.github.julekarenalender

import com.github.julekarenalender.config.{DefaultConfigurationModule, AppInfo}
import com.github.julekarenalender.log.Logging
import view.MainGui

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
    } text("Scans the images/ folder for participants. No GUI is launched")
    opt[Unit]("reset") optional() action {
      (_, c) =>
        c.copy(reset = true)
    } text("Resets all configuration. Use at own risk! No GUI is launched")
    opt[Unit]("bonus") optional() action {
      (_, c) =>
        c.copy(bonus = true)
    } text("Enables the bonus wheel")
    opt[Unit]("debug") optional() action {
      (_, c) =>
        c.copy(debug = true)
    } text("Turns on debug mode")
    help("help") text ("prints this usage text")
  }

  parser.parse(args, Config()) map {
    config =>
      if(config.debug) logger.enableDebug()
      if(config.reset) reset(config)
      if(config.scan) scan(config)
      launchGui(config)
  } getOrElse {
    logger.error("Unable to parse arguments")
  }

  private def scan(config: Config) {
    val configModule = new DefaultConfigurationModule(config)
    logger.info("Scanning images/ and importing participants...")
    configModule.importParticipants()
    sys.exit(0)
  }

  private def reset(config: Config) {
    val configModule = new DefaultConfigurationModule(config)
    logger.info("Resetting all data...")
    configModule.reset()
    if(!config.scan) sys.exit(0)
  }

  private def launchGui(config: Config) {
    MainGui.launch(config)
  }
}
