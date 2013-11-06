package com.github.julekarenalender

case class Participant(id: Option[Int] = None,
                       name: String,
                       image: String,
                       win: Int)