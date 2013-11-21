package com.github.julekarenalender.view

import com.github.julekarenalender.Config
import no.jervell.view.MainWindow
import com.github.julekarenalender.config.{AppInfo, DefaultConfigurationModule, Parser}
import javax.swing.{UnsupportedLookAndFeelException, UIManager, SwingUtilities}
import com.github.julekarenalender.log.Logging

object MainGui extends Logging {
  def launch(config: Config) {
    val days = Parser.toDays(config.days)
    val configModule = new DefaultConfigurationModule(config)

    SwingUtilities.invokeLater {
      new Runnable {
        def run() {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

            new MainWindow(days, configModule).display()
          } catch {
            case ex@(_: ClassNotFoundException |
                     _: InstantiationException |
                     _: IllegalAccessException |
                     _: UnsupportedLookAndFeelException) => logger.error("Unable to start look and feel", ex)
            case ex: Throwable => logger.error(s"Unable to start ${AppInfo.ProgramName}", ex); ex.printStackTrace()
          }
        }
      }
    }
  }
}
