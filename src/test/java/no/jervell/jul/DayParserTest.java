package no.jervell.jul;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.matchers.JUnitMatchers.hasItems;

public class DayParserTest {
    private List<String> dayArguments = new ArrayList<String>();
    DayParser dayParser;

    @Before
    public void setUp() {
        dayParser = new DayParser(dayArguments);
    }

    @Test
    public void shouldReturnListWithOnlyTodaysDateIfNoOtherDaysAreSpecified() {
        int[] days = dayParser.parse();

        hasItems(days, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void shouldParseListToIntArray() {
        populateDayListWithArray("10", "11", "14", "15");

        int[] days = dayParser.parse();

        hasItems(days, 10, 11, 14, 15);
    }

    private void populateDayListWithArray(String... dayArray) {
        for (int i = 0; i < dayArray.length; i++) {
            dayArguments.add(dayArray[i]);
        }
    }
}
