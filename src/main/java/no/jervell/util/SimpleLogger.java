package no.jervell.util;

public class SimpleLogger {
    private boolean debug = false;
    private boolean info = false;

    private SimpleLogger() {
    }

    private static class SimpleLoggerHolder {
        public static final SimpleLogger INSTANCE = new SimpleLogger();
    }

    public static SimpleLogger getInstance() {
        return SimpleLoggerHolder.INSTANCE;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    public void debug(String text) {
        if (debug) {
            System.out.println(text);
        }
    }

    public void info(String text) {
        if (info) {
            System.out.println(text);
        }
    }

    public void error(String text) {
        System.out.println(text);
    }
}
