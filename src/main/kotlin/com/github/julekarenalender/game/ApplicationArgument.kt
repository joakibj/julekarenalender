package com.github.julekarenalender.game

import com.github.julekarenalender.App
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

fun parseArguments(args: Array<String>): ApplicationArguments {
    val parser = ArgParser("${App.appName.toLowerCase()}-${App.version}", useDefaultHelpShortName = true)

    val days by parser.option(
        ArgType.String,
        shortName = "d",
        description = "Days separated by comma where there should be a draw. Ex: --days 1,2,5,5"
    )
    val bonus by parser.option(ArgType.Boolean, shortName = "b", description = "Enable the bonus wheel")
    val scan by parser.option(
        ArgType.Boolean,
        shortName = "s",
        description = "Scan for participants in the images-folder. Filename = name of participant"
    )
    val dryRun by parser.option(
        ArgType.Boolean,
        shortName = "x",
        description = "Dry run. Feel free to play around! Winners wont saved. Database wont be updated. Use it in combination with --scan to test import :)"
    )
    val list by parser.option(ArgType.Boolean, shortName = "l", description = "List participants")
    val reset by parser.option(ArgType.Boolean, description = "Resets all configuration. Danger, danger! ")
    val debug by parser.option(ArgType.Boolean, shortName = "D", description = "Turn on debug mode")

    parser.parse(args)
    return ApplicationArguments(
        days,
        bonus == true,
        debug == true,
        scan == true,
        dryRun == true,
        list == true,
        reset == true
    )
}

data class ApplicationArguments(
    val days: String?,
    val bonus: Boolean,
    val debug: Boolean,
    val scan: Boolean,
    val dryRun: Boolean,
    val list: Boolean,
    val reset: Boolean
)
