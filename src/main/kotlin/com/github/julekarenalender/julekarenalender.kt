package com.github.julekarenalender


import com.github.julekarenalender.domain.Game
import com.github.julekarenalender.domain.GameParameters
import com.github.julekarenalender.game.*
import com.github.julekarenalender.logger.Logger
import no.jervell.view.MainWindow
import org.dizitart.no2.Nitrite
import java.awt.EventQueue
import javax.swing.UIManager
import kotlin.system.exitProcess


object App {
    const val appName = "Julekarenalender"
    const val version = "3.0.0"
    const val title = "$appName v$version"
}

lateinit var db: Nitrite
lateinit var logger: Logger
lateinit var currentGame: Game
lateinit var gameParameters: GameParameters

fun main(args: Array<String>) {
    val appArgs = parseArguments(args)

    db = connectDb()
    logger = Logger(appArgs.debug)
    gameParameters = createGameParameters(appArgs)
    currentGame = createCurrentGame(appArgs)

    if (appArgs.reset) resetAllData()
    if (appArgs.list) listParticipants()

    if (gameParameters.dryRun) logger.info("*** DryRun: No database updates")

    EventQueue.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        MainWindow().display()
    }
}






