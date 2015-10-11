package com.mathieuclement.nextbus.backend.controller;

import com.mathieuclement.nextbus.backend.model.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public List<Connection> getNextConnections(
            @RequestParam(value = "latitude") float latitude,
            @RequestParam(value = "longitude") float longitude) {
        List<Connection> nextConnections = new LinkedList<>();
        Agency agency = new Agency(123L, "CFF", "Europe/Zurich");
        nextConnections.add(new Connection(
                Instant.now(),
                agency,
                new Stop("123", "FRK", "Furka", 1.234567f, 2.345678f, "F"),
                new Route("R1", agency, "Short route name", "Long route name", RouteType.BUS)
        ));
        return nextConnections;
    }

}
