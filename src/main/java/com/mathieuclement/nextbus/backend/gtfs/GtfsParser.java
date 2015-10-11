package com.mathieuclement.nextbus.backend.gtfs;

import com.mathieuclement.nextbus.backend.db.repository.CalendarDateRepository;
import com.mathieuclement.nextbus.backend.db.repository.TripRepository;
import com.mathieuclement.nextbus.backend.model.CalendarDate;
import com.mathieuclement.nextbus.backend.model.RouteType;
import com.mathieuclement.nextbus.backend.model.Trip;
import com.mathieuclement.nextbus.backend.model.converter.LocalDateConverter;
import org.apache.commons.csv.*;
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

    private static final CSVFormat INPUT_CSV_FORMAT = CSVFormat
            .DEFAULT
            .withQuoteMode(QuoteMode.NON_NUMERIC)
            .withHeader();

    private static final CSVFormat OUTPUT_CSV_FORMAT = CSVFormat
            .DEFAULT
            .withQuoteMode(QuoteMode.ALL);

    public static void writeAgencies(File agenciesFile, PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(agenciesFile, CHARSET, INPUT_CSV_FORMAT)) {
            for (CSVRecord record : records) {
                csvPriter.printRecord(record.get("agency_id"), record.get("agency_name"), record.get("agency_timezone"));
            }

        }
        csvPriter.close();
    }

    public static void writeCalendarDates(File calendarDatesFile, PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(calendarDatesFile, CHARSET, INPUT_CSV_FORMAT)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateConverter localDateConverter = new LocalDateConverter();
            for (CSVRecord record : records) {
                csvPriter.printRecord(record.get("service_id"),
                        localDateConverter.convertToDatabaseColumn(LocalDate.parse(record.get("date"), dateFormatter)));
            }
        }
        csvPriter.close();
    }

    public static void writeRoutes(File routesFile, PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(routesFile, CHARSET, INPUT_CSV_FORMAT)) {
            for (CSVRecord record : records) {
                csvPriter.printRecord(
                        record.get("route_id"),
                        record.get("agency_id"),
                        record.get("route_short_name"),
                        record.get("route_long_name"),
                        RouteType.values()[Integer.parseInt(record.get("route_type"))].name());
            }
        }
        csvPriter.close();
    }

    public static void writeStops(File stopsFile, PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(stopsFile, CHARSET, INPUT_CSV_FORMAT)) {
            for (CSVRecord record : records) {
                csvPriter.printRecord(record.get("stop_id"),
                        record.get("stop_code"),
                        record.get("stop_name"),
                        record.get("stop_lat"),
                        record.get("stop_lon"),
                        record.get("platform_code"));
            }
        }
        csvPriter.close();
    }

    public static void writeTrips(File tripsFile, PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(tripsFile, CHARSET, INPUT_CSV_FORMAT)) {
            for (CSVRecord record : records) {
                csvPriter.printRecord(record.get("trip_id"),
                        record.get("service_id"),
                        record.get("route_id"),
                        record.get("trip_headsign"),
                        record.get("trip_short_name"));
            }
        }
        csvPriter.close();
    }

    public static void writeStopTimes(File file,
                                      TripRepository tripRepository,
                                      CalendarDateRepository calendarDateRepository,
                                      PrintWriter writer) throws IOException {
        CSVPrinter csvPriter = new CSVPrinter(writer, OUTPUT_CSV_FORMAT);
        try (CSVParser records = CSVParser.parse(file, CHARSET, INPUT_CSV_FORMAT)) {
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
                        csvPriter.printRecord(tripId,
                                gtfsDate.toInstant(calendarDate.getLocalDate(), zoneId).toString(),
                                stopId,
                                stopSequence);
                    }
                }
            }
        }
        csvPriter.close();
    }

    private static String escapeSql(String sql) {
        return sql.replace("'", "''");
    }

}
