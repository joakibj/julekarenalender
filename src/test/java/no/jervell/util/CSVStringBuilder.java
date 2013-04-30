package no.jervell.util;

import java.util.ArrayList;
import java.util.List;

public class CSVStringBuilder implements Builder<String> {
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String CSV_HEADER = "Name;Picture;Day;Note";

    private List<String> lines = new ArrayList<String>();

    public static CSVStringBuilder getCSVStringBuilder() {
        return new CSVStringBuilder();
    }

    public CSVStringBuilder addLine(String line) {
        lines.add(line);
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append(CSV_HEADER).append(NEWLINE);
        int newLineCounter = 0;
        for (String line : lines) {
            builder.append(line);
            newLineCounter++;
            if (newLineCounter < lines.size()) {
                builder.append(NEWLINE);
            }
        }

        return builder.toString();
    }
}
