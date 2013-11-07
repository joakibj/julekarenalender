package com.github.julekarenalender.repository

trait UsingDatabaseConnection extends UsingDatabaseDriver {

  import driver.profile.simple._

  def database = Database.forURL(driver.connUrl, driver = driver.driverName)
}
