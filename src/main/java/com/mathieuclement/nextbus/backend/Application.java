package com.mathieuclement.nextbus.backend;

import com.mathieuclement.nextbus.backend.db.repository.AgencyRepository;
import com.mathieuclement.nextbus.backend.gtfs.GtfsParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application implements ResourceLoaderAware {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner populateDb(AgencyRepository agencyRepository) {
        return args -> {
            agencyRepository.save(GtfsParser.toAgencies(getFile(AGENCY_FILENAME)).values());
            System.out.println(agencyRepository.findOne(97L));
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
