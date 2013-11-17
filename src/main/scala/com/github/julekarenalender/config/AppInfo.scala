package com.github.julekarenalender.config

object AppInfo {
  val ProgramName = "Julekarenalender"
  val Version = "2.0.0-SNAPSHOT"

  def title = toString

  override def toString = s"$ProgramName $Version"
}
