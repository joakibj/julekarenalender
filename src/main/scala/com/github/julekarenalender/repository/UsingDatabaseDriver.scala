package com.github.julekarenalender.repository

import scala.slick.driver.ExtendedProfile

trait UsingDatabaseDriver {
  val driver: DatabaseDriver
}
