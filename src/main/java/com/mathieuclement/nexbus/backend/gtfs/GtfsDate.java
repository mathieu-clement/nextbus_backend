package com.mathieuclement.nexbus.backend.gtfs;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class GtfsDate {
    private int hours;
    private int minutes;
    private int seconds;

    public static GtfsDate fromOverOrEqualTo24Hours(String hourMinuteSecond) {
        int[] ints = toIntArray(hourMinuteSecond, ":");
        GtfsDate gtfsDate = new GtfsDate();
        gtfsDate.hours = ints[0];
        gtfsDate.minutes = ints[1];
        gtfsDate.seconds = ints[2];
        return gtfsDate;
    }

    public Instant toInstant(LocalDate localDate, ZoneId zoneId) {
        // Weird logic from GTFS reference
        int hoursToAdd = hours;
        int hoursToAdd2 = 0;
        if (hoursToAdd > 23) {
            hoursToAdd = 23;
            hoursToAdd2 = hoursToAdd - 23;
        }

        return localDate
                .atStartOfDay()
                .plusHours(hoursToAdd)
                .plusHours(hoursToAdd2)
                .plusMinutes(minutes)
                .plusSeconds(seconds)
                .atZone(zoneId)
                .toInstant();
    }

    private static int[] toIntArray(String s, String delimiter) {
        String[] strings = s.split(delimiter);
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }
}
