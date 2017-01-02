package cases;

import java.util.concurrent.TimeUnit;

import model.Task;

/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

public class SlowCycle extends Task {

    private static final int INITIAL_WORKLOAD = 1024;
    public static final int CYCLE_DEADLINE_MILLIS = 700;
    public static final int CYCLE_TIME_MILLIS = 1200;

    public SlowCycle() {
        super(TimeUnit.MILLISECONDS.toNanos(CYCLE_DEADLINE_MILLIS), TimeUnit.MILLISECONDS.toNanos(CYCLE_TIME_MILLIS),
                INITIAL_WORKLOAD);
    }

}
