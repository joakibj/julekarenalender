package com.github.julekarenalender.game

import com.github.julekarenalender.*
import com.github.julekarenalender.domain.Game
import com.github.julekarenalender.domain.GameParameters
import com.github.julekarenalender.domain.ParticipantData
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.File
import java.time.LocalDate
import kotlin.system.exitProcess

fun connectDb(): Nitrite {
    val n = nitrite {
        file = File("${App.appName.toLowerCase()}.db")
        autoCommitBufferSize = 2048
        compress = true
        autoCompact = false
        autoCommit = true
    }
    println() // Just some spacing between Nitrite's SLF4J warnings.. 🙄 B/c life is too short
    println()
    return n
}

fun listParticipants() {
    if (currentGame.participants.isEmpty()) {
        logger.info("Found no participants")
    } else {
        currentGame.participants.forEach(::println)
    }
    exitProcess(0)
}

fun resetAllData() {
    if (gameParameters.dryRun) {
        logger.info("*** DryRun: Nice try.. ;) Not touching that database. Exiting..")
        exitProcess(0)
    }
    logger.info("Cleaning database..")
    db.getRepository(ParticipantData::class.java).drop()
    db.commit()
    db.close()
    logger.info("Done.")

    exitProcess(0)
}

fun createGameParameters(applicationArguments: ApplicationArguments): GameParameters {
    val isDryRun = applicationArguments.dryRun
    val enableBonusGame = applicationArguments.bonus
    val daysAsList: List<Int> = if (!applicationArguments.days.isNullOrBlank()) applicationArguments.days.split(",").map(Integer::parseInt) else listOf(if (LocalDate.now().dayOfMonth > 24) 1 else LocalDate.now().dayOfMonth) // yolo

    return GameParameters(days = daysAsList, bonus = enableBonusGame, dryRun = isDryRun)
}

fun createCurrentGame(applicationArguments: ApplicationArguments): Game {
    val participants = loadParticipants(applicationArguments)
    return Game(participants = participants, gameParameters = gameParameters)
}
