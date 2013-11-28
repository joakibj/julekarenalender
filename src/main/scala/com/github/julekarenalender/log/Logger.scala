package com.github.julekarenalender.log

object Logger extends Logger with ConsoleLogger

trait Logger {
  def enableDebug()
  def debug(logLine: String)
  def info(logLine: String)
  def error(logLine: String)
  def error(logLine: String, thrown: Throwable)
}