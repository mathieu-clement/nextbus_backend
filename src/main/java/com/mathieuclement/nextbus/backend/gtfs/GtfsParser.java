package com.mathieuclement.nextbus.backend.gtfs;

import com.mathieuclement.nextbus.backend.db.repository.CalendarDateRepository;
import com.mathieuclement.nextbus.backend.db.repository.TripRepository;
import com.mathieuclement.nextbus.backend.model.CalendarDate;
import com.mathieuclement.nextbus.backend.model.RouteType;
import com.mathieuclement.nextbus.backend.model.Trip;
import com.mathieuclement.nextbus.backend.model.converter.LocalDateConverter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

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

    public static void writeAgencies(File agenciesFile, PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(agenciesFile, CHARSET, CSV_FORMAT)) {
            for (CSVRecord record : records) {
                String idStr = record.get("agency_id");
                String name = record.get("agency_name");
                String timeZoneName = record.get("agency_timezone");

                writer.append("INSERT INTO AGENCY (ID, NAME, TIMEZONE_NAME) VALUES (")
                        .append(idStr)
                        .append(", '").append(escapeSql(name))
                        .append("', '").append(escapeSql(timeZoneName))
                        .append("');")
                        .println();
            }
        }
        writer.close();
    }

    public static void writeCalendarDates(File calendarDatesFile, PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(calendarDatesFile, CHARSET, CSV_FORMAT)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateConverter localDateConverter = new LocalDateConverter();
            for (CSVRecord record : records) {
                String serviceId = record.get("service_id");
                String date = record.get("date");

                writer.append("INSERT INTO CALENDAR_DATE (SERVICE_ID, LOCAL_DATE) VALUES ('")
                        .append(escapeSql(serviceId))
                        .append("', '")
                        .append(escapeSql(localDateConverter.convertToDatabaseColumn(LocalDate.parse(date, dateFormatter))))
                        .append("');")
                        .println();
            }
        }
        writer.close();
    }

    public static void writeRoutes(File routesFile, PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(routesFile, CHARSET, CSV_FORMAT)) {
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                String agencyId = record.get("agency_id");
                String routeShortName = record.get("route_short_name");
                String routeLongName = record.get("route_long_name");
                int routeTypeOrd = Integer.parseInt(record.get("route_type"));
                RouteType routeType = RouteType.values()[routeTypeOrd];

                writer.append("INSERT INTO ROUTE (ID, AGENCY_ID, SHORT_NAME, LONG_NAME, ROUTE_TYPE) VALUES ('")
                        .append(escapeSql(routeId))
                        .append("', ").append(agencyId)
                        .append(", '").append(escapeSql(routeShortName))
                        .append(", '").append(escapeSql(routeLongName))
                        .append(", '").append(routeType.name())
                        .append("');")
                        .println();
            }
        }
        writer.close();
    }

    public static void writeStops(File stopsFile, PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(stopsFile, CHARSET, CSV_FORMAT)) {
            for (CSVRecord record : records) {
                String stopId = record.get("stop_id");
                String stopCode = record.get("stop_code");
                String stopName = record.get("stop_name");
                String latitude = record.get("stop_lat");
                String longitude = record.get("stop_lon");
                String platformCode = record.get("platform_code");

                writer.append("INSERT INTO STOP (ID, STOP_CODE, STOP_NAME, LATITUDE, LONGITUDE, PLATFORM_CODE) VALUES ('")
                        .append(escapeSql(stopId))
                        .append("', '").append(escapeSql(stopCode))
                        .append("', '").append(escapeSql(stopName))
                        .append("', ").append(latitude)
                        .append(", ").append(longitude)
                        .append(", '").append(escapeSql(platformCode))
                        .append("');")
                        .println();
            }
        }
    }

    public static void writeTrips(File tripsFile, PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(tripsFile, CHARSET, CSV_FORMAT)) {
            for (CSVRecord record : records) {
                String routeId = record.get("route_id");
                String serviceId = record.get("service_id");
                String tripId = record.get("trip_id");
                String tripHeadSign = record.get("trip_headsign");
                String tripShortName = record.get("trip_short_name");

                writer.append("INSERT INTO TRIP (ID, SERVICE_ID, ROUTE_ID, HEAD_SIGN, SHORT_NAME) VALUES ('")
                        .append(escapeSql(tripId))
                        .append("', '").append(escapeSql(serviceId))
                        .append("', '").append(escapeSql(routeId))
                        .append("', '").append(escapeSql(tripHeadSign))
                        .append("', '").append(escapeSql(tripShortName))
                        .append("');")
                        .println();
            }
        }
    }

    public static void writeStopTimes(File stopTimesFile,
                                      TripRepository tripRepository,
                                      CalendarDateRepository calendarDateRepository,
                                      PrintWriter writer) throws IOException {
        try (CSVParser records = CSVParser.parse(stopTimesFile, CHARSET, CSV_FORMAT)) {
            for (CSVRecord record : records) {
                String tripId = record.get("trip_id");
                Trip trip = tripRepository.findOne(tripId);
                Assert.notNull(trip);

                ZoneId zoneId = ZoneId.of(trip.getRoute().getAgency().getTimeZone().getID());

                GtfsDate gtfsDate = GtfsDate.fromOverOrEqualTo24Hours(record.get("departure_time"));

                String stopSequence = record.get("stop_sequence");
                String stopId = record.get("stop_id");

                String serviceId = trip.getServiceId();

                Collection<CalendarDate> calendarDates = calendarDateRepository.findByServiceId(serviceId);

                if (calendarDates != null) {
                    for (CalendarDate calendarDate : calendarDates) {
                        writer.append("INSERT INTO STOP_TIME (TRIP_ID, DEPARTURE_DATETIME, STOP_ID, STOP_SEQUENCE) VALUES ('")
                                .append(escapeSql(tripId))
                                .append("', '").append(gtfsDate.toInstant(calendarDate.getDate(), zoneId).toString())
                                .append("', '").append(escapeSql(stopId))
                                .append("', ").append(stopSequence)
                                .append(");")
                                .println();
                    }
                }
            }
        }
    }

    private static String escapeSql(String sql) {
        return sql.replace("'", "''");
    }

}
