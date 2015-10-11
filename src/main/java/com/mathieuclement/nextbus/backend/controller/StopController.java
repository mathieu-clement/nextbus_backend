package com.mathieuclement.nextbus.backend.controller;

import com.mathieuclement.nextbus.backend.db.repository.StopWithDistanceRepository;
import com.mathieuclement.nextbus.backend.model.StopWithDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stop")
public class StopController {

    @Autowired
    private StopWithDistanceRepository stopWithDistanceRepository;

    @RequestMapping(value = "/closest", method = RequestMethod.GET)
    public List<StopWithDistance> getClosestStops(
            @RequestParam(value = "maxNbResults", defaultValue = "50") int maxResults,
            @RequestParam(value = "latitude") float latitude,
            @RequestParam(value = "longitude") float longitude,
            @RequestParam(value = "maxDist") int maxDist // meters
    ) {
        Assert.isTrue(maxResults < 51, "Number of results is too large.");
        Assert.isTrue(maxDist < 10_001, "Distance is too large.");
        List<StopWithDistance> closestStopsByDistance = stopWithDistanceRepository.findStopsByDistance(latitude, longitude, maxDist, maxResults);
        return closestStopsByDistance;
    }

}
