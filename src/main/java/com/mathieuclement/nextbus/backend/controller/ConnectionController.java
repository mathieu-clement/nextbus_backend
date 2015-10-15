package com.mathieuclement.nextbus.backend.controller;

import com.mathieuclement.nextbus.backend.db.repository.ConnectionRepository;
import com.mathieuclement.nextbus.backend.db.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    public static final int ONE_DAY_IN_MINUTES = 24 * 60 + 1;
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private StopRepository stopRepository;

    @RequestMapping(value = "/buses/next/{stopId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getNextBuses(
            @PathVariable(value = "stopId") String stopId,
            @RequestParam(value = "maxMinutes", defaultValue = "120") long maxMinutes,
            HttpServletResponse servletResponse) {

        Assert.isTrue(maxMinutes < ONE_DAY_IN_MINUTES, "Search limited to 24 hours.");

        if (!stopRepository.exists(stopId)) {
            return new ResponseEntity<>("The provided Stop does not exist.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(connectionRepository.findNextBuses(
                stopId,
                Date.from(Instant.now().plus(Duration.ofMinutes(maxMinutes)))
        ), HttpStatus.OK);
    }

}
