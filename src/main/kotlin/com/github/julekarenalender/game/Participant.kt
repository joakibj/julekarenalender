package com.github.julekarenalender.game

import com.github.julekarenalender.db
import com.github.julekarenalender.domain.ParticipantData
import com.github.julekarenalender.logger
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.system.exitProcess

private val allowedExtensions = listOf("jpg", "jpeg", "png", "gif")

fun scanForParticipants(): MutableList<ParticipantData>? {

    logger.info("Scanning images-folder and importing participants...")

    val path = Paths.get("images").toAbsolutePath().toString()
    if (!File(path).exists()) {
        logger.error("Could not find images-folder. Expected: $path")
        exitProcess(1)
    }

    val participants = Arrays.stream(File(path).listFiles())
        .filter { it.extension.toLowerCase() in allowedExtensions }
        .map { ParticipantData(name = it.nameWithoutExtension, image = it.name) }
        .collect(Collectors.toUnmodifiableList())

    logger.info("Ho! Ho! Ho! Found ${participants.size} participants :D")
    if (logger.isDebug) participants.forEach(::println)

    participants.forEach { it.insertNonexistentFirstTime() }

    return participants
}


fun loadParticipants(appArgs: ApplicationArguments): List<ParticipantData> {
    val maybeParticipants = if (appArgs.scan) {
        scanForParticipants() // and save to database
    } else {
        db.getRepository(ParticipantData::class.java).find().toList()
    }
    if (maybeParticipants == null || maybeParticipants.isEmpty()) {
        logger.error("Found no participants!")
        return syntheticTestDataGenerator()
    }
    return maybeParticipants
}

fun syntheticTestDataGenerator(): List<ParticipantData> {
    val firstnames = mutableListOf("Important", "Traditional", "Successful Søvnig", "Useful", "Comprehensive")
    val lastnames = mutableListOf("Bird", "Family", "Music", "Bottle", "Suggestion")
    firstnames.shuffle()
    lastnames.shuffle()

    logger.info("*** Generating synthetic test data...")
    logger.info("*** Successfully generated a total of ${lastnames.size} participants")

    var count = 0
    return lastnames.stream()
        .map { ParticipantData(name = "${firstnames[count]} $it", image = "$it.jpg") }
        .peek { count++ }
        .collect(Collectors.toList())
}
