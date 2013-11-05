package no.jervell.jul;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import no.jervell.repository.PersonDAO;
import no.jervell.repository.impl.CSVFile;
import no.jervell.repository.impl.DefaultPersonDAO;
import no.jervell.util.SimpleLogger;
import no.jervell.view.MainWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class Julekarenalender {
    // JCommander parameters
    @Parameter(description = "[Liste av tall med hvilke dager det skal foregå trekning]")
    private List<String> days = new ArrayList<String>();

    @Parameter(names = "-debug", description = "Start program i debug modus")
    private boolean debug = false;

    @Parameter(names = "-help", help = true, description = "Denne beskjeden")
    private boolean help;

    public static final String PROGRAM_NAME = "Julekarenalender";

    private PersonDAO personDAO;
    private MainWindow mainWindow;

    public static void main(String[] args) {
        try {
            Julekarenalender julekalender = new Julekarenalender();
            julekalender.parseArguments(args);
            julekalender.loadDataSource();
            julekalender.runMainWindow();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void parseArguments(String[] args) {
        JCommander jCommander = new JCommander(this, args);
        jCommander.setProgramName(PROGRAM_NAME);
        if (help) {
            jCommander.usage();
            System.exit(0);
        }
        SimpleLogger.getInstance().setDebug(debug);
        SimpleLogger.getInstance().setInfo(true);
    }

    private void runMainWindow() {
        mainWindow = new MainWindow(parseDays(), personDAO);
        mainWindow.display();
    }

    private int[] parseDays() {
        DayParser dayParser = new DayParser(days);
        return dayParser.parse();
    }

    private void loadDataSource() throws IOException {
        File resourceFile = new File(".", "julekarenalender.csv");
        SimpleLogger.getInstance().info("Loading configuration from: " + resourceFile);
        CSVFile dataSource = new CSVFile(resourceFile, true);
        personDAO = new DefaultPersonDAO(dataSource);
    }
}
