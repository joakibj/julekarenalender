
name := "julekarenalender"

version := "2.0.0"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8")

libraryDependencies += "com.beust" % "jcommander" % "1.29"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"
