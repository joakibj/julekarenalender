package com.github.julekarenalender

case class Participant(var id: Option[Int] = None,
                       var name: String,
                       var image: String,
                       var win: Int)