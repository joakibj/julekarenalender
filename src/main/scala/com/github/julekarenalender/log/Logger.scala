package com.github.julekarenalender.log

trait Logging {
  val logger: Logger = Logger
}

object Logger extends Logger with ConsoleLogger

trait ConsoleLogger extends Logger {
  private var debug: Boolean = false

  override def enableDebug() {
    debug = true
  }

  override def debug(logLine: String) {
    if(debug) println(logLine)
  }

  override def info(logLine: String) {
    println(logLine)
  }

  override def error(logLine: String) {
    Console.err.println(logLine)
  }
}

trait Logger {
  def enableDebug()
  def debug(logLine: String)
  def info(logLine: String)
  def error(logLine: String)
}