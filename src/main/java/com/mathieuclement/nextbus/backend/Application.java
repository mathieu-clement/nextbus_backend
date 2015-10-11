package com.mathieuclement.nextbus.backend;

import com.mathieuclement.nextbus.backend.db.repository.CalendarDateRepository;
import com.mathieuclement.nextbus.backend.db.repository.TripRepository;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.*;
import java.net.URISyntaxException;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableJpaRepositories("com.mathieuclement.nextbus.backend.db.repository")
//@PropertySource("")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner populateDb(CalendarDateRepository calendarDateRepository,
                                        TripRepository tripRepository)
            throws IOException, URISyntaxException {

        return args -> {

            Driver.setLogLevel(2);

            /*
            LOG.info("Parsing agencies");
            GtfsParser.writeAgencies(getFile(AGENCY_FILENAME), getPrintWriterForFile("agency.csv"));

            LOG.info("Parsing calendar dates");
            GtfsParser.writeCalendarDates(getFile(CALENDAR_DATES_FILENAME), getPrintWriterForFile("calendar_date.csv"));

            LOG.info("Parsing routes");
            GtfsParser.writeRoutes(getFile(ROUTES_FILENAME), getPrintWriterForFile("route.csv"));

            LOG.info("Parsing stops");
            GtfsParser.writeStops(getFile(STOPS_FILENAME), getPrintWriterForFile("stop.csv"));

            LOG.info("Parsing trips");
            GtfsParser.writeTrips(getFile(TRIPS_FILENAME), getPrintWriterForFile("trip.csv"));

            LOG.info("Parsing stop times");
            GtfsParser.writeStopTimes(new File("/home/mathieu/gtfs_bus/stop_times.txt"), tripRepository, calendarDateRepository,
                    getPrintWriterForFile("stop_time.csv"));
            */
        };
    }

    private PrintWriter getPrintWriterForFile(String filename) throws IOException {
        File file = new File("/home/mathieu/CSV/" + filename);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")), true);
    }

    private static final String AGENCY_FILENAME = "agency.txt";
    private static final String ROUTES_FILENAME = "routes.txt";
    private static final String TRIPS_FILENAME = "trips.txt";
    private static final String STOPS_FILENAME = "stops.txt";
    private static final String CALENDAR_DATES_FILENAME = "calendar_dates.txt";
    private static final String STOP_TIMES_FILENAME = "stop_times.txt";
}
