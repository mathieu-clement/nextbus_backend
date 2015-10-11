package com.mathieuclement.nextbus.backend;

import com.mathieuclement.nextbus.backend.db.repository.CalendarDateRepository;
import com.mathieuclement.nextbus.backend.db.repository.TripRepository;
import com.mathieuclement.nextbus.backend.gtfs.GtfsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application implements ResourceLoaderAware {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner populateDb(CalendarDateRepository calendarDateRepository,
                                        TripRepository tripRepository)
            throws IOException, URISyntaxException {

        return args -> {
            LOG.info("Parsing agencies");
            GtfsParser.writeAgencies(getFile(AGENCY_FILENAME), getPrintWriterForFile("agency.sql"));

            LOG.info("Parsing calendar dates");
            GtfsParser.writeCalendarDates(getFile(CALENDAR_DATES_FILENAME), getPrintWriterForFile("calendar_date.sql"));

            LOG.info("Parsing routes");
            GtfsParser.writeRoutes(getFile(ROUTES_FILENAME), getPrintWriterForFile("route.sql"));

            LOG.info("Parsing stops");
            GtfsParser.writeStops(getFile(STOPS_FILENAME), getPrintWriterForFile("stop.sql"));

            LOG.info("Parsing trips");
            GtfsParser.writeTrips(getFile(TRIPS_FILENAME), getPrintWriterForFile("trip.sql"));

            /*
            LOG.info("Parsing stop times");
            GtfsParser.writeStopTimes(getFile(STOP_TIMES_FILENAME), tripRepository, calendarDateRepository,
                    getPrintWriterForFile("stop_time.sql"));
                    */
        };
    }

    private PrintWriter getPrintWriterForFile(String filename) throws IOException {
        File file = new File("C:\\Users\\macl\\Documents\\SQL\\" + filename);
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

    private ResourceLoader resourceLoader;

    private File getFile(String filename) throws URISyntaxException, IOException {
        Resource resource = getResource("classpath:/gtfs_bus/" + filename);
        Path resourcePath = Paths.get(resource.getURI());
        return resourcePath.toFile();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }
}
