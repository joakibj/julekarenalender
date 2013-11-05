package no.jervell.jul;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DayParser {
    private List<String> days;

    public DayParser(List<String> days) {
        this.days = days;
    }

    public int[] parse() {
        if (days.size() == 0) {
            return new int[]{Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};
        } else {
            int i = 0;
            int[] result = new int[days.size()];
            for (String day : days) {
                result[i] = Integer.parseInt(day);
                i++;
            }
            Arrays.sort(result);
            return result;
        }
    }
}
