package no.jervell.repository.impl;

import no.jervell.util.SimpleLogger;

import java.io.*;
import java.util.*;

public class CSVFile {
    private final static String EMPTY = "";
    private String lineSeparator = System.getProperty("line.separator");
    private char cellSeparator = ';';
    private File file;
    private List<String> header;    // null if file has no header row
    private List<List<String>> rows;
    private Map<String, Integer> columns;

    public CSVFile(File file, boolean hasHeaderRow) throws IOException {
        this.file = file;
        this.rows = new ArrayList<List<String>>();
        read(hasHeaderRow);
    }

    private void read(boolean headerRow) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        if (headerRow) {
            readHeader(reader);
        }
        appendAllRows(reader);
        trimEmptyTrailingRows();
        reader.close();
    }

    public void save() throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
        BufferedWriter writer = new BufferedWriter(streamWriter);
        writer.write(toCSV());
        writer.close();
        SimpleLogger.getInstance().info("Configuration has been saved to " + file);
    }

    private void readHeader(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("No header found");
        }
        header = parseRow(line);
        this.columns = enumerateColumns(header);
    }

    private Map<String, Integer> enumerateColumns(List<String> header) {
        Map<String, Integer> columns = new HashMap<String, Integer>();
        for (int i = 0; i < header.size(); ++i) {
            columns.put(canonicalColumnName(header.get(i)), i);
        }
        return columns;
    }

    private String canonicalColumnName(String columnName) {
        return columnName == null ? null : columnName.toUpperCase();
    }


    private int getColumnIndex(String columnName) {
        columnName = canonicalColumnName(columnName);
        Integer result = columns.get(columnName);
        if (result == null) {
            throw new IllegalArgumentException("No such column name: " + columnName);
        } else {
            return result;
        }
    }

    private void appendAllRows(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            appendRow(line);
        }
    }

    private void trimEmptyTrailingRows() {
        for (int i = rows.size() - 1; i >= 0; --i) {
            if (isEmpty(getRowReference(i))) {
                rows.remove(i);
            }
        }
    }

    private boolean isEmpty(List<String> row) {
        for (String s : row) {
            if (s != null && s.length() > 0) {
                return false;
            }
        }
        return true;
    }

    private void appendRow(String line) {
        List<String> row = parseRow(line);
        appendRow(row);
    }

    private void appendRow(List<String> row) {
        rows.add(row);
    }

    private List<String> parseRow(String line) {
        List<String> row = new ArrayList<String>();
        String[] cells = line.split(String.valueOf(cellSeparator));
        row.addAll(Arrays.asList(cells));
        return row;
    }

    public int getRowCount() {
        return rows.size();
    }

    public String get(int rowIndex, int columnIndex) {
        List<String> row = getRowReference(rowIndex);
        if (columnIndex < 0 || columnIndex >= row.size()) {
            return null;
        }
        return row.get(columnIndex);
    }

    public String get(int rowIndex, String columnName) {
        return get(rowIndex, getColumnIndex(columnName));
    }

    public int get(int rowIndex, int columnIndex, int defaultValue) {
        String value = get(rowIndex, columnIndex);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int get(int rowIndex, String columnName, int defaultValue) {
        return get(rowIndex, getColumnIndex(columnName), defaultValue);
    }

    public void set(int rowIndex, int columnIndex, String value) {
        List<String> row = getRowReference(rowIndex);
        while (columnIndex >= row.size()) {
            row.add(EMPTY);
        }
        row.set(columnIndex, value);
    }

    public void set(int rowIndex, String columnName, String value) {
        set(rowIndex, getColumnIndex(columnName), value);
    }

    public void appendRow(String... values) {
        rows.add(new ArrayList<String>(Arrays.asList(values)));
    }

    private List<String> getRowReference(int rowIndex) {
        return rows.get(rowIndex);
    }

    private List<String> getRowCopy(int rowIndex) {
        return new ArrayList<String>(getRowReference(rowIndex));
    }

    public List<String> getRow(int rowIndex) {
        return getRowCopy(rowIndex);
    }

    public String toCSV() {
        String result = toCSV(rows);
        if (header != null) {
            result = toCSVRow(header) + lineSeparator + result;
        }
        return result;
    }

    private String toCSV(List<List<String>> rows) {
        StringBuilder fileBuilder = new StringBuilder();
        boolean useSeparator = false;
        for (List<String> row : rows) {
            if (useSeparator) {
                fileBuilder.append(lineSeparator);
            } else {
                useSeparator = true;
            }
            fileBuilder.append(toCSVRow(row));
        }
        return fileBuilder.toString();
    }

    private String toCSVRow(List<String> row) {
        StringBuilder rowBuilder = new StringBuilder();
        boolean useSeparator = false;
        for (String cell : row) {
            if (useSeparator) {
                rowBuilder.append(cellSeparator);
            } else {
                useSeparator = true;
            }
            rowBuilder.append(cell == null ? EMPTY : cell);
        }
        return rowBuilder.toString();
    }

    @Override
    public String toString() {
        return toCSV();
    }
}