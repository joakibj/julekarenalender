import sbt._
import Keys._

object MainBuild extends Build {
  lazy val project = Project(
    id = "julekarenalender",
    base = file("."),
    settings = Project.defaultSettings ++ Seq()
  )
}
