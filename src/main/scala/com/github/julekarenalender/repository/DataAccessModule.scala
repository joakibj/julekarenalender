package com.github.julekarenalender.repository

import scala.slick.driver.ExtendedProfile

class DataAccessModule(override val driver: DatabaseDriver) extends UsingDatabaseConfiguration with UsingDatabaseDriver {

}
