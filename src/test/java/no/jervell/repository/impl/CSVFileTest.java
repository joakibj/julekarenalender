package no.jervell.repository.impl;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.util.List;

@RunWith(JUnit4.class)
public class CSVFileTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private CSVFile dataSource;

    private String newLine = System.getProperty("line.separator");

    @Test
    public void shouldReadDefaultCSVFile() throws IOException {
        String csv = "Name;Picture;Day;Note" + newLine +
                "Ole;ole.jpg;1;Borte" + newLine +
                "Dole;dole.jpg;2;" + newLine +
                "Doffen;doffen.jpg;3;" + newLine +
                "Donald;donald.jpg;";
        File file = createTemporaryFileWithContents("default.csv", csv);
        dataSource = new CSVFile(file, true);
        assertThat(dataSource.getRowCount(), is(4));

        assertDataRow(dataSource.getRow(0), "Ole", "ole.jpg", "1", "Borte", 4);
        assertDataRow(dataSource.getRow(1), "Dole", "dole.jpg", "2", null, 3);
        assertDataRow(dataSource.getRow(2), "Doffen", "doffen.jpg", "3", null, 3);
        assertDataRow(dataSource.getRow(3), "Donald", "donald.jpg", null, null, 2);

        assertThat(dataSource.get(0, "Name"), is("Ole"));
        assertThat(dataSource.get(1, "Name"), is("Dole"));
        assertThat(dataSource.get(2, "Name"), is("Doffen"));
        assertThat(dataSource.get(3, "Name"), is("Donald"));

        assertThat(dataSource.get(3, "Day", -99), is(-99));
    }

    @Test
    public void shouldReadAndSaveCSVFileAfterChanges() throws IOException {
        String csv = "Name;Picture;Day;Note" + newLine +
                "Ole;ole.jpg;1;Borte" + newLine +
                "Dole;dole.jpg;2;" + newLine +
                "Doffen;doffen.jpg;3;" + newLine +
                "Donald;donald.jpg;";
        File file = createTemporaryFileWithContents("default.csv", csv);
        dataSource = new CSVFile(file, true);

        dataSource.set(3, "Name", "Dolly");
        dataSource.set(3, "Picture", "dolly.jpg");
        dataSource.set(3, "Day", "26");

        dataSource.save();

        String expected = "Name;Picture;Day;Note" + newLine +
                "Ole;ole.jpg;1;Borte" + newLine +
                "Dole;dole.jpg;2" + newLine +
                "Doffen;doffen.jpg;3" + newLine +
                "Dolly;dolly.jpg;26";

        String actual = readFileToString(file);
        assertThat(actual, is(expected));
    }

    private void assertDataRow(List<String> cells, String name, String fileName, String dayWon, String note, int expectedSize) {
        assertThat(cells.size(), is(expectedSize));
        assertThat(cells.get(0), is(name));
        assertThat(cells.get(1), is(fileName));
        if (dayWon != null) {
            assertThat(cells.get(2), is(dayWon));
        }
        if (note != null) {
            assertThat(cells.get(3), is(note));
        }
    }

    private File createTemporaryFileWithContents(String filename, String contents) throws IOException {
        File file = tmpFolder.newFile(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(contents);
        bufferedWriter.close();
        return file;
    }

    private String readFileToString(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringWriter sw = new StringWriter();
            char[] buffer = new char[1024 * 4];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                sw.write(buffer, 0, n);
            }
            String readString = sw.toString();
            reader.close();
            return readString;
        } catch (FileNotFoundException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }
}
