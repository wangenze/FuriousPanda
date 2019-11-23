package com.wez.panda.servo.driver;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DriverParameters {
    public static final DriverParameters DEFAULT_PARAMETERS = DriverParameters.builder()
            .delayAfterInitialization(3_000L)
            .minInterval(100L)
            .build();

    private final long delayAfterInitialization;
    private final long minInterval;
}
