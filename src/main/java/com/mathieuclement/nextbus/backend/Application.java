package com.mathieuclement.nextbus.backend;

import com.mathieuclement.nextbus.backend.db.repository.*;
import com.mathieuclement.nextbus.backend.gtfs.GtfsParser;
import com.mathieuclement.nextbus.backend.model.CalendarDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@SpringBootApplication
public class Application implements ResourceLoaderAware {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner populateDb(AgencyRepository agencyRepository,
                                        CalendarDateRepository calendarDateRepository,
                                        RouteRepository routeRepository,
                                        StopRepository stopRepository,
                                        StopTimeRepository stopTimeRepository,
                                        TripRepository tripRepository) throws IOException, URISyntaxException {

        if (agencyRepository.count() == 0) {
            LOG.info("Importing agencies");
            agencyRepository.save(GtfsParser.toAgencies(getFile(AGENCY_FILENAME)));
            LOG.info("Imported agencies");
        }

        if (calendarDateRepository.count() == 0) {
            LOG.info("Importing calendar dates");
            Collection<CalendarDate> calendarDates = GtfsParser.toCalendarDates(getFile(CALENDAR_DATES_FILENAME));
            for (CalendarDate calendarDate : calendarDates) {
                entityManager.persist(calendarDate);
            }
            LOG.info("Imported calendar dates");
        }

        if (routeRepository.count() == 0) {
            LOG.info("Importing routes");
            routeRepository.save(GtfsParser.toRoutes(getFile(ROUTES_FILENAME), agencyRepository));
            LOG.info("Imported routes");
        }

        if (stopRepository.count() == 0) {
            LOG.info("Importing stops");
            stopRepository.save(GtfsParser.toStops(getFile(STOPS_FILENAME)));
            LOG.info("Imported stops");
        }

        if (tripRepository.count() == 0) {
            LOG.info("Importing trips");
            tripRepository.save(GtfsParser.toTrips(getFile(TRIPS_FILENAME), routeRepository));
            LOG.info("Imported trips");
        }

        if (stopTimeRepository.count() == 0) {
            LOG.info("Importing stop times");
            stopTimeRepository.save(GtfsParser.toStopTimes(getFile(STOP_TIMES_FILENAME),
                    tripRepository, stopRepository, calendarDateRepository));
            LOG.info("Imported stop times");
        }

        return args -> {
        };
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
