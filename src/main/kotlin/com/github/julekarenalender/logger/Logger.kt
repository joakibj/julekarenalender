package com.github.julekarenalender.logger

class Logger(val isDebug: Boolean = false) {

    fun debug(str: String) {
        if (isDebug) {
            println("DEBUG: $str")
        }
    }

    fun info(str: String) {
        println(str)
    }

    fun warn(str: String) {
        println("Ups! $str")
    }

    fun error(str: String) {
        println("Oh no! $str")
    }

    fun error(str: String, thr: Throwable) {
        println("Oh no! $str ${thr.javaClass}: ${thr.message}")
    }

}