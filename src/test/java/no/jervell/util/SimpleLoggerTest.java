package no.jervell.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

//this test breaks the junit runner in sbt
@Ignore
public class SimpleLoggerTest {
    private static final String DEBUG_TEXT = "Debug text";
    private static final String INFO_TEXT = "Info text";
    private static final String ERROR_TEXT = "Error text";
    private static final String NEWLINE = System.getProperty("line.separator");

    private final ByteArrayOutputStream normalOutput = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(normalOutput));
        System.setErr(new PrintStream(errorOutput));
        resetLoggerStates();
    }

    private void resetLoggerStates() {
        SimpleLogger.getInstance().setDebug(false);
        SimpleLogger.getInstance().setInfo(false);
    }

    @Test
    public void shouldOutputDebugInfo() {
        SimpleLogger.getInstance().setDebug(true);
        SimpleLogger.getInstance().debug(DEBUG_TEXT);

        assertThat(normalOutput.toString(), is(DEBUG_TEXT + NEWLINE));
    }

    @Test
    public void shouldNotOutputDebugInfo() {
        SimpleLogger.getInstance().debug(DEBUG_TEXT);

        assertThat(normalOutput.toString(), is(""));
    }

    @Test
    public void shouldOutputInfo() {
        SimpleLogger.getInstance().setInfo(true);
        SimpleLogger.getInstance().info(INFO_TEXT);

        assertThat(normalOutput.toString(), is(INFO_TEXT + NEWLINE));
    }

    @Test
    public void shouldNotOutputInfo() {
        SimpleLogger.getInstance().info(INFO_TEXT);

        assertThat(normalOutput.toString(), is(""));
    }

    @Test
    public void shouldOutputError() {
        SimpleLogger.getInstance().error(ERROR_TEXT);

        assertThat(errorOutput.toString(), is(ERROR_TEXT + NEWLINE));
    }

    @After
    public void cleanUp() {
        System.setOut(null);
        System.setErr(null);
    }
}
