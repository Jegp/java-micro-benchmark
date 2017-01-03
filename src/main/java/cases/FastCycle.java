/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */
package cases;

import java.util.concurrent.TimeUnit;

import model.Task;

/**
 * A faster cycle with a deadline of 10ms, a cycle time of 40ms and an initial workload of 16.
 *
 * @see Task
 */
public class FastCycle extends Task {

    private static final int INITIAL_WORKLOAD = 16;
    public static final int CYCLE_DEADLINE_MILLIS = 10;
    public static final int CYCLE_HERTZ_MILLIS = 25;
    public static final int CYCLE_TIME_MILLIS = 1000 / CYCLE_HERTZ_MILLIS;

    public FastCycle() {
        super(TimeUnit.MILLISECONDS.toNanos(CYCLE_DEADLINE_MILLIS), TimeUnit.MILLISECONDS.toNanos(CYCLE_TIME_MILLIS),
                INITIAL_WORKLOAD);
    }

}
