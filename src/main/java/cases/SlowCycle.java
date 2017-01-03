package cases;

import java.util.concurrent.TimeUnit;

import model.Task;

/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

/**
 * A slow cycle with a deadline of 700ms, a cycle time of 1200ms and an initial workload of 1024.
 *
 * @see Task
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
