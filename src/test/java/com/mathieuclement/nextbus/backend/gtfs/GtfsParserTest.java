package com.mathieuclement.nextbus.backend.gtfs;

import com.mathieuclement.nextbus.backend.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
        // DBUnit...
})
*/
public class GtfsParserTest {

    public static final String AGENCY_FILENAME = "agency.txt";
    public static final String ROUTES_FILENAME = "routes.txt";
    public static final String TRIPS_FILENAME = "trips.txt";
    public static final String STOPS_FILENAME = "stops.txt";
    public static final String CALENDAR_DATES_FILENAME = "calendar_dates.txt";
    public static final String STOP_TIMES_FILENAME = "stop_times.txt";

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private CalendarDateRepository calendarDateRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private StopTimeRepository stopTimeRepository;

    @Autowired
    private TripRepository tripRepository;

    /*
    @Test
    public void testToAgencies() throws Exception {
        Collection<Agency> agencies = GtfsParser.toAgencies(getFile(AGENCY_FILENAME));
        long agencyId = 832L;
        String expectedAgencyName = "AWA (Autobetrieb Weesen-Amden)";
        String expectedTimeZoneName = "Europe/Berlin";
        assertTrue(agencies.contains(new Agency(agencyId, expectedAgencyName, expectedTimeZoneName)));

        Agency weesenAgency = agencyRepository.findOne(agencyId);
        assertEquals(expectedAgencyName, weesenAgency.getName());
        assertEquals(expectedTimeZoneName, weesenAgency.getTimeZone().getID());
    }

    @Test
    public void testToCalendarDates() throws Exception {
        String serviceId = "230217:1:s";
        LocalDate date = LocalDate.of(2015, 8, 2);

        Collection<CalendarDate> dates = calendarDateRepository.findByServiceId(serviceId);
        assertTrue(dates != null);
        assertTrue(dates.contains(new CalendarDate(serviceId, date)));
    }

    @Test
    public void testToRoutes() throws Exception {
        // Mockup agencies
        Map<Long, Agency> agencies = new HashMap<>(1);
        Agency agency97 = new Agency(97L,
                "TRAVYS-y (Transports Vallee de Joux-Yverdon-Ste-Croix (ystec))", // TODO There should be an e acute in Vallee
                "Europe/Berlin");
        agencies.put(97L, agency97);

        Collection<Route> routes = GtfsParser.toRoutes(getFile(ROUTES_FILENAME), agencyRepository);

        String routeId = "01451.000097";
        String shortName = "1451";
        String longName = "BUS 1451";
        RouteType routeType = RouteType.BUS;

        assertTrue(routes.contains(new Route(routeId, agency97, shortName, longName, routeType)));
    }

    @Test
    public void testToStops() throws Exception {
        Collection<Stop> stops = GtfsParser.toStops(getFile(STOPS_FILENAME));
        String stopId = "8508722";
        assertTrue(stops.contains(new Stop(stopId, "", "Jegenstorf, Rotonda", 47.058165f, 7.510826f, "")));
    }

    @Test
    public void testToStopTimes() throws Exception {
        // Agency
        Agency agency97 = new Agency(97L,
                "TRAVYS-y (Transports Vallee de Joux-Yverdon-Ste-Croix (ystec))", // TODO There should be an e acute in Vallee
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
        Collection<StopTime> stopTimes = GtfsParser.toStopTimes(getFile(STOP_TIMES_FILENAME), tripRepository, stopRepository, calendarDateRepository);

        assertTrue(stopTimes.contains(new StopTime(trip, Instant.parse("2015-07-14T05:26:00.00Z"), stop, 1)));
    }

    @Test
    public void testToTrips() throws Exception {
        Collection<Trip> trips = GtfsParser.toTrips(getFile(TRIPS_FILENAME), routeRepository);
        Route route1451 = routeRepository.findOne("01451.000097");
        assertTrue(route1451 != null);
        String tripId = "761261";
        assertTrue(trips.contains(new Trip(tripId, "761261:1:s", route1451, "Baulmes", "1451")));
    }
    */

    private static File getFile(String filename) throws URISyntaxException {
        URL resourceUrl = GtfsParserTest.class.getResource("/gtfs_bus/" + filename);
        Path resourcePath = Paths.get(resourceUrl.toURI());
        return resourcePath.toFile();
    }
}