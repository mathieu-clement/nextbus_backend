package com.mathieuclement.nextbus.backend.gtfs;

import com.mathieuclement.nextbus.backend.db.repository.*;
import com.mathieuclement.nextbus.backend.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(GtfsParser.class);

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
    public static Collection<Agency> toAgencies(File agenciesFile) throws IOException {
        try (CSVParser records = CSVParser.parse(agenciesFile, CHARSET, CSV_FORMAT)) {
            Collection<Agency> agencies = new Vector<>(150);
            for (CSVRecord record : records) {
                long id = Long.parseLong(record.get("agency_id"));
                String name = record.get("agency_name");
                String timeZoneName = record.get("agency_timezone");

                agencies.add(new Agency(id, name, timeZoneName));
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
    public static Collection<CalendarDate> toCalendarDates(File calendarDatesFile) throws IOException {
        try (CSVParser records = CSVParser.parse(calendarDatesFile, CHARSET, CSV_FORMAT)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            Collection<CalendarDate> calendarDates = new HashSet<>();
            for (CSVRecord record : records) {
                String serviceId = record.get("service_id");
                String date = record.get("date");

                calendarDates.add(new CalendarDate(serviceId,
                        LocalDate.parse(date, dateFormatter)));
            }
            return calendarDates;
        }
    }

    public static Collection<Route> toRoutes(File routesFile, AgencyRepository agencyRepository) throws IOException {
        try (CSVParser records = CSVParser.parse(routesFile, CHARSET, CSV_FORMAT)) {
            Collection<Route> routes = new HashSet<>(500_000);
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                long agencyId = Long.parseLong(record.get("agency_id"));
                Agency agency = agencyRepository.findOne(agencyId);
                String routeShortName = record.get("route_short_name");
                String routeLongName = record.get("route_long_name");
                int routeTypeOrd = Integer.parseInt(record.get("route_type"));
                RouteType routeType = RouteType.values()[routeTypeOrd];

                routes.add(new Route(routeId, agency, routeShortName, routeLongName, routeType));
            }
            return routes;
        }
    }

    public static Collection<Stop> toStops(File stopsFile) throws IOException {
        try (CSVParser records = CSVParser.parse(stopsFile, CHARSET, CSV_FORMAT)) {
            Collection<Stop> stops = new HashSet<>(25_000);
            for (CSVRecord record : records) {
                String stopId = record.get("stop_id");
                String stopCode = record.get("stop_code");
                String stopName = record.get("stop_name");
                float latitude = Float.parseFloat(record.get("stop_lat"));
                float longitude = Float.parseFloat(record.get("stop_lon"));
                String platformCode = record.get("platform_code");

                stops.add(new Stop(stopId, stopCode, stopName, latitude, longitude, platformCode));
            }
            return stops;
        }
    }

    public static Collection<StopTime> toStopTimes(File stopTimesFile,
                                                   TripRepository tripRepository,
                                                   StopRepository stopRepository,
                                                   CalendarDateRepository calendarDateRepository) throws IOException {
        try (CSVParser records = CSVParser.parse(stopTimesFile, CHARSET, CSV_FORMAT)) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Collection<StopTime> stopTimes = new TreeSet<>();
            int lastPercents = 0;
            for (CSVRecord record : records) {
                long percents = records.getRecordNumber() / 93681;
                if (percents != lastPercents) {
                    System.out.print("\r" + (++lastPercents) + " %");
                }
                String tripId = record.get("trip_id");
                Trip trip = tripRepository.findOne(tripId);
                Assert.notNull(trip);

                TimeZone timeZone = trip.getRoute().getAgency().getTimeZone();
                ZoneId zoneId = ZoneId.of(timeZone.getID());

                String departureTime = record.get("departure_time");
                GtfsDate gtfsDate = GtfsDate.fromOverOrEqualTo24Hours(departureTime);

                int stopSequence = Integer.parseInt(record.get("stop_sequence"));
                String stopId = record.get("stop_id");
                Stop stop = stopRepository.findOne(stopId);
                Assert.notNull(stop);

                String serviceId = trip.getServiceId();

                Collection<CalendarDate> calendarDates = calendarDateRepository.findByServiceId(serviceId);

                if (calendarDates != null) {
                    for (CalendarDate calendarDate : calendarDates) {
                        Instant instant = gtfsDate.toInstant(calendarDate.getDate(), zoneId);
                        StopTime stopTime = new StopTime(trip, instant, stop, stopSequence);
                        stopTimes.add(stopTime);
                    }
                }
            }
            stopWatch.stop();
            System.out.println();
            Marker marker = MarkerFactory.getMarker("ToStopTimes");
            LOG.debug(marker, "Short summary: " + stopWatch.shortSummary());
            LOG.debug(marker, "Pretty print: " + stopWatch.prettyPrint());
            LOG.debug(marker, "Execution time in seconds: " + stopWatch.getTotalTimeSeconds());
            return stopTimes;
        }
    }

    public static Collection<Trip> toTrips(File tripsFile, RouteRepository routeRepository) throws IOException {
        try (CSVParser records = CSVParser.parse(tripsFile, CHARSET, CSV_FORMAT)) {
            Collection<Trip> trips = new TreeSet<>();
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                Route route = routeRepository.findOne(routeId);
                Assert.notNull(route);
                String serviceId = record.get("service_id");
                String tripId = record.get("trip_id");
                String tripHeadSign = record.get("trip_headsign");
                String tripShortName = record.get("trip_short_name");

                trips.add(new Trip(tripId, serviceId, route, tripHeadSign, tripShortName));
            }
            return trips;
        }
    }

}
