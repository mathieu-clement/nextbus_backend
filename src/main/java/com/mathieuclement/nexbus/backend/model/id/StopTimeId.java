package com.mathieuclement.nexbus.backend.model.id;

import com.mathieuclement.nexbus.backend.model.Stop;
import com.mathieuclement.nexbus.backend.model.Trip;

import java.time.Instant;

public class StopTimeId {
    private Trip trip;
    private Stop stop;
    private Instant departureTime;
}
