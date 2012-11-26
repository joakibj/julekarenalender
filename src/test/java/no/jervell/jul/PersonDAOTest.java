package no.jervell.jul;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import no.jervell.file.CSVFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

@RunWith(JUnit4.class)
public class PersonDAOTest {

    @Test
    public void shouldLoadDataFromCSVFile() throws IOException {
        CSVFile dataSourceMock = Mockito.mock(CSVFile.class);
        Mockito.when(dataSourceMock.getRowCount()).thenReturn(4);
        for(int i = 0; i < 4; i++) {
            Mockito.when(dataSourceMock.get(i, "name")).thenReturn("Name" + i);
            Mockito.when(dataSourceMock.get(i, "picture")).thenReturn("name" + i + ".jpg");
            Mockito.when(dataSourceMock.get(i, "day", 0)).thenReturn(i);
        }

        PersonDAO personDAO = new PersonDAO(dataSourceMock);

        List<Person> personer = personDAO.getPersonList();
        assertThat(personer.size(), is(4));
        int i = 0;
        for(Person p : personer) {
            assertThat(p.getName(), is("Name"+i));
            assertThat(p.getPicture(), is("name"+i+".jpg"));
            assertThat(p.getDay(), is(i));
            i++;
        }
    }

}
