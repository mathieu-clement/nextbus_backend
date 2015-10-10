package com.mathieuclement.nexbus.backend.gtfs;

import com.mathieuclement.nexbus.backend.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

public class GtfsParserTest {

    public static final String AGENCY_FILENAME = "agency.txt";
    public static final String ROUTES_FILENAME = "routes.txt";
    public static final String TRIPS_FILENAME = "trips.txt";
    public static final String STOPS_FILENAME = "stops.txt";
    public static final String CALENDAR_DATES_FILENAME = "calendar_dates.txt";
    public static final String STOP_TIMES_FILENAME = "stop_times.txt";

    private static Map<String, Set<CalendarDate>> calendarDates;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.print("Preparing calendar dates... ");
        calendarDates = GtfsParser.toCalendarDates(getFile(CALENDAR_DATES_FILENAME));
        System.out.println("OK");
    }

    @Test
    public void testToAgencies() throws Exception {
        Map<Long, Agency> agencies = GtfsParser.toAgencies(getFile(AGENCY_FILENAME));
        long agencyId = 832L;
        String expectedAgencyName = "AWA (Autobetrieb Weesen-Amden)";
        String expectedTimeZoneName = "Europe/Berlin";
        assertTrue(agencies.containsValue(new Agency(agencyId, expectedAgencyName, expectedTimeZoneName)));

        assertTrue(agencies.containsKey(agencyId));
        Agency weesenAgency = agencies.get(agencyId);
        assertEquals(expectedAgencyName, weesenAgency.getName());
        assertEquals(expectedTimeZoneName, weesenAgency.getTimeZone().getID());
    }

    @Test
    public void testToCalendarDates() throws Exception {
        String serviceId = "230217:1:s";
        LocalDate date = LocalDate.of(2015, 8, 2);

        assertTrue(calendarDates.containsKey(serviceId));
        assertTrue(calendarDates.get(serviceId).contains(new CalendarDate(serviceId, date, true)));
    }

    @Test
    public void testToRoutes() throws Exception {
        // Mockup agencies
        Map<Long, Agency> agencies = new HashMap<>(1);
        Agency agency97 = new Agency(97L,
                "TRAVYS-y (Transports Vallée de Joux-Yverdon-Ste-Croix (ystec))",
                "Europe/Berlin");
        agencies.put(97L, agency97);

        Map<String, Route> routes = GtfsParser.toRoutes(getFile(ROUTES_FILENAME), agencies);

        String routeId = "01451.000097";
        String shortName = "1451";
        String longName = "BUS 1451";
        RouteType routeType = RouteType.BUS;

        assertTrue(routes.containsKey(routeId));
        assertEquals(new Route(routeId, agency97, shortName, longName, routeType), routes.get(routeId));
    }

    @Test
    public void testToStops() throws Exception {
        Map<String, Stop> stops = GtfsParser.toStops(getFile(STOPS_FILENAME));
        String stopId = "8508722";
        assertTrue(stops.containsKey(stopId));
        assertEquals(new Stop(stopId, "", "Jegenstorf, Rotonda", 47.058165f, 7.510826f, ""), stops.get(stopId));
    }

    @Test
    public void testToStopTimes() throws Exception {
        // Agency
        Agency agency97 = new Agency(97L,
                "TRAVYS-y (Transports Vallée de Joux-Yverdon-Ste-Croix (ystec))",
                "Europe/Berlin");

        // Route
        String routeId = "01451.000097";
        String shortName = "1451";
        String longName = "BUS 1451";
        RouteType routeType = RouteType.BUS;
        Route route1451 = new Route(routeId, agency97, shortName, longName, routeType);

        // Trip
        String serviceId = "761261:1:s";
        Trip trip = new Trip("761261", serviceId, route1451, "Baulmes", "1451");

        // Stop
        Stop stop = new Stop("8579109", "", "Baulmes, poste", 46.789791f, 6.522299f, "");

        // Stop times
        Map<Long, Agency> agencies = GtfsParser.toAgencies(getFile(AGENCY_FILENAME));
        Map<String, Route> routes = GtfsParser.toRoutes(getFile(ROUTES_FILENAME), agencies);
        Map<String, Trip> trips = GtfsParser.toTrips(getFile(TRIPS_FILENAME), routes);
        Map<String, Stop> stops = GtfsParser.toStops(getFile(STOPS_FILENAME));
        Map<Stop, Map<Trip, Set<StopTime>>> result =
                GtfsParser.toStopTimes(getFile(STOP_TIMES_FILENAME), trips, stops, calendarDates);

        assertTrue(result.containsKey(stop));
        Map<Trip, Set<StopTime>> tripsForStop = result.get(stop);

        assertTrue(tripsForStop.containsKey(trip));
        Set<StopTime> stopTimesForTrip = tripsForStop.get(trip);

        assertTrue(stopTimesForTrip.contains(new StopTime(trip, Instant.parse("2015-07-14T05:26:00.00Z"), stop, 1)));
    }

    @Test
    public void testToTrips() throws Exception {
        Map<Long, Agency> agencies = GtfsParser.toAgencies(getFile(AGENCY_FILENAME));
        Map<String, Route> routes = GtfsParser.toRoutes(getFile(ROUTES_FILENAME), agencies);
        Map<String, Trip> trips = GtfsParser.toTrips(getFile(TRIPS_FILENAME), routes);
        assertTrue(routes.containsKey("01451.000097"));
        Route route1451 = routes.get("01451.000097");
        String tripId = "761261";
        assertTrue(trips.containsKey(tripId));
        assertEquals(new Trip(tripId, "761261:1:s", route1451, "Baulmes", "1451"), trips.get(tripId));
    }

    private static File getFile(String filename) throws URISyntaxException {
        URL resourceUrl = GtfsParserTest.class.getResource("/gtfs_bus/" + filename);
        Path resourcePath = Paths.get(resourceUrl.toURI());
        return resourcePath.toFile();
    }
}