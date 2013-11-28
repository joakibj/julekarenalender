package com.github.julekarenalender.log

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

  override def error(logLine: String, thrown: Throwable) {
    Console.err.println(s"$logLine. ${thrown.getClass}: ${thrown.getMessage}")
  }
}
