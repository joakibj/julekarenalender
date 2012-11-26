package no.jervell.file;

import static org.junit.Assert.assertThat;
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

    @Test
    public void shouldReadDefaultCSVFile() throws IOException {
        String csv = "Name;Picture;Day;Note\n" +
                "Ole;ole.jpg;1;Borte\n" +
                "Dole;dole.jpg;2;\n" +
                "Doffen;doffen.jpg;3;\n" +
                "Donald;donald.jpg;";
        File file = createTemporaryFileWithContents("default.csv", csv);
        dataSource = new CSVFile(file, true);
        assertThat(dataSource.getRowCount(), is(4));

        List<String> cells = dataSource.getRow(0);
        assertThat(cells.size(), is(4));
        assertThat(cells.get(0), is("Ole"));
        assertThat(cells.get(1), is("ole.jpg"));
        assertThat(cells.get(2), is("1"));
        assertThat(cells.get(3), is("Borte"));

        cells = dataSource.getRow(1);
        assertThat(cells.size(), is(3));
        assertThat(cells.get(0), is("Dole"));
        assertThat(cells.get(1), is("dole.jpg"));
        assertThat(cells.get(2), is("2"));

        cells = dataSource.getRow(3);
        assertThat(cells.size(), is(2));
        assertThat(cells.get(0), is("Donald"));
        assertThat(cells.get(1), is("donald.jpg"));

        assertThat(dataSource.get(0, "Name"), is("Ole"));
        assertThat(dataSource.get(1, "Name"), is("Dole"));
        assertThat(dataSource.get(2, "Name"), is("Doffen"));
        assertThat(dataSource.get(3, "Name"), is("Donald"));

        assertThat(dataSource.get(3, "Day", -99), is(-99));
    }

    private File createTemporaryFileWithContents(String filename, String contents) throws IOException {
        File file = tmpFolder.newFile(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(contents);
        bufferedWriter.close();
        return file;
    }
}
