package com.mathieuclement.nexbus.backend.gtfs;

import com.mathieuclement.nexbus.backend.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GtfsParser {

    /**
     * Charset used by all files
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Format of all (CSV) files
     */
    private static final CSVFormat CSV_FORMAT = CSVFormat
            .DEFAULT
            .withQuoteMode(QuoteMode.NON_NUMERIC)
            .withHeader();

    /**
     * Make Agency objects from a CSV file
     *
     * @param agenciesFile a CSV file usually named "agency.txt"
     * @return agencies
     * @throws IOException if the file can't be opened / read / parsed
     */
    public static Map<Long, Agency> toAgencies(File agenciesFile) throws IOException {
        try (CSVParser records = CSVParser.parse(agenciesFile, CHARSET, CSV_FORMAT)) {
            Map<Long, Agency> agencies = new HashMap<>(150);
            for (CSVRecord record : records) {
                long id = Long.parseLong(record.get("agency_id"));
                String name = record.get("agency_name");
                String timeZoneName = record.get("agency_timezone");

                agencies.put(id, new Agency(id, name, timeZoneName));
            }
            return agencies;
        }
    }

    /**
     * Make CalendarDate objects from a CSV file
     *
     * @param calendarDatesFile a CSV file usually named "calendar_dates.txt"
     * @return calendar dates
     * @throws IOException if the file can't be opened / read / parsed
     */
    public static Map<String, Set<CalendarDate>> toCalendarDates(File calendarDatesFile) throws IOException {
        try (CSVParser records = CSVParser.parse(calendarDatesFile, CHARSET, CSV_FORMAT)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            Map<String, Set<CalendarDate>> calendarDates = new TreeMap<>();
            for (CSVRecord record : records) {
                String serviceId = record.get("service_id");
                String date = record.get("date");
                int exceptionType = Integer.parseInt(record.get("exception_type"));
                Assert.isTrue(exceptionType == 1 || exceptionType == 2);

                Set<CalendarDate> calendarDateSet = calendarDates.get(serviceId);
                if (calendarDateSet == null) {
                    calendarDateSet = new TreeSet<>();
                    calendarDates.put(serviceId, calendarDateSet);
                }

                calendarDateSet.add(new CalendarDate(serviceId,
                        LocalDate.parse(date, dateFormatter),
                        exceptionType == 1));
            }
            return calendarDates;
        }
    }

    /**
     * Make Route objects from a CSV file
     *
     * @param routesFile a CSV file usually named
     * @param agencies   the agencies referenced by the routes
     * @return routes
     * @throws IOException if the file can't be opened / read / parsed
     */
    public static Map<String, Route> toRoutes(File routesFile, Map<Long, Agency> agencies) throws IOException {
        try (CSVParser records = CSVParser.parse(routesFile, CHARSET, CSV_FORMAT)) {
            Map<String, Route> routes = new HashMap<>(500_000);
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                long agencyId = Long.parseLong(record.get("agency_id"));
                Agency agency = agencies.get(agencyId);
                String routeShortName = record.get("route_short_name");
                String routeLongName = record.get("route_long_name");
                int routeTypeOrd = Integer.parseInt(record.get("route_type"));
                RouteType routeType = RouteType.values()[routeTypeOrd];

                routes.put(routeId, new Route(routeId, agency, routeShortName, routeLongName, routeType));
            }
            return routes;
        }
    }

    public static Map<String, Stop> toStops(File stopsFile) throws IOException {
        try (CSVParser records = CSVParser.parse(stopsFile, CHARSET, CSV_FORMAT)) {
            Map<String, Stop> stops = new HashMap<>(25_000);
            for (CSVRecord record : records) {
                String stopId = record.get("stop_id");
                String stopCode = record.get("stop_code");
                String stopName = record.get("stop_name");
                float latitude = Float.parseFloat(record.get("stop_lat"));
                float longitude = Float.parseFloat(record.get("stop_lon"));
                String platformCode = record.get("platform_code");

                stops.put(stopId, new Stop(stopId, stopCode, stopName, latitude, longitude, platformCode));
            }
            return stops;
        }
    }

    /**
     * Returns, for each stop all trips, and for each trip all departure times
     *
     * @param stopTimesFile a CSV file, usually named "stop_times.txt"
     * @param trips         the trips
     * @param stops         the stops
     * @param calendarDates the calendar dates
     * @return for each stop all trips, and for each trip all departure times
     * @throws IOException if the file can't be opened / read / parsed
     */
    public static Map<Stop, Map<Trip, Set<StopTime>>> toStopTimes(File stopTimesFile,
                                                                  Map<String, Trip> trips,
                                                                  Map<String, Stop> stops,
                                                                  Map<String, Set<CalendarDate>> calendarDates) throws IOException {
        try (CSVParser records = CSVParser.parse(stopTimesFile, CHARSET, CSV_FORMAT)) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Map<Stop, Map<Trip, Set<StopTime>>> result = new TreeMap<>();
            int lastPercents = 0;
            for (CSVRecord record : records) {
                long percents = records.getRecordNumber() / 93681;
                if (percents != lastPercents) {
                    System.out.print("\r" + (++lastPercents) + " %");
                }
                String tripId = record.get("trip_id");
                Trip trip = trips.get(tripId);
                Assert.notNull(trip);

                TimeZone timeZone = trip.getRoute().getAgency().getTimeZone();
                ZoneId zoneId = ZoneId.of(timeZone.getID());

                String departureTime = record.get("departure_time");
                GtfsDate gtfsDate = GtfsDate.fromOverOrEqualTo24Hours(departureTime);

                int stopSequence = Integer.parseInt(record.get("stop_sequence"));
                String stopId = record.get("stop_id");
                Stop stop = stops.get(stopId);
                Assert.notNull(stop);

                String serviceId = trip.getServiceId();

                if (calendarDates.containsKey(serviceId)) {
                    Set<CalendarDate> serviceDates = calendarDates.get(serviceId);
                    Set<StopTime> stopTimes = new TreeSet<>();
                    for (CalendarDate calendarDate : serviceDates) {
                        Instant instant = gtfsDate.toInstant(calendarDate.getDate(), zoneId);
                        StopTime stopTime = new StopTime(trip, instant, stop, stopSequence);
                        stopTimes.add(stopTime);
                    }

                    Map<Trip, Set<StopTime>> tripsForStop = result.get(stop);
                    if (tripsForStop == null) {
                        tripsForStop = new TreeMap<>();
                        result.put(stop, tripsForStop);
                    }
                    tripsForStop.put(trip, stopTimes);
                }
            }
            stopWatch.stop();
            System.out.println();
            System.out.println("Short summary:");
            System.out.println(stopWatch.shortSummary());
            System.out.println("Pretty print:");
            System.out.println(stopWatch.prettyPrint());
            System.out.println("Execution time in seconds: " + stopWatch.getTotalTimeSeconds());
            return result;
        }
    }

    public static Map<String, Trip> toTrips(File tripsFile, Map<String, Route> routes) throws IOException {
        try (CSVParser records = CSVParser.parse(tripsFile, CHARSET, CSV_FORMAT)) {
            Map<String, Trip> trips = new HashMap<>(600_000);
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                Route route = routes.get(routeId);
                Assert.notNull(route);
                String serviceId = record.get("service_id");
                String tripId = record.get("trip_id");
                String tripHeadSign = record.get("trip_headsign");
                String tripShortName = record.get("trip_short_name");

                trips.put(tripId, new Trip(tripId, serviceId, route, tripHeadSign, tripShortName));
            }
            return trips;
        }
    }

}
