package com.github.julekarenalender

import java.io.File

case class Participant(id: Int = 0,
                       name: String,
                       image: File,
                       win: Int) {
}