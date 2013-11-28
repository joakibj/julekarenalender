package com.github.julekarenalender.config

object AppInfo {
  val ProgramName = BuildInfo.name
  val Version = BuildInfo.version

  def title = toString

  override def toString = s"$ProgramName $Version"
}
