/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package model;

import benchmark.LoadGenerator;
import benchmark.LoadIterator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * A task that can run an indefinite number of iterations with varying duration (period) and deadline requirements
 * as well as an initial workload to indicate how much 'work' to do per iteration.
 *
 * @author jepeders
 */
public class Task {

    public final long periodInNanos;
    public final long deadlineInNanos;
    public final int initialWorkload;

    /**
     * Creates a task with a deadline and period in the given unit.
     *
     * @param deadlineInMs The maximum time it can take to perform one iteration.
     * @param periodInMs   The time interval between the beginning of iterations.
     */
    @JsonCreator
    public Task(@JsonProperty("deadlineInNanos") long deadlineInNanos, @JsonProperty("periodInNanos") long periodInNanos,
                @JsonProperty("initalWorkload") int initialWorkload) {
        this.deadlineInNanos = deadlineInNanos;
        this.periodInNanos = periodInNanos;
        this.initialWorkload = initialWorkload;
    }

    public LoadIterator getLoadIterator(int iterations) {
        return LoadIterator.ofBoundAndSize(initialWorkload, iterations);
    }

    /**
     * An iteration of the task that uses the {@link LoadGenerator} given in the constructor to generate an amount of
     * 'load'.
     *
     * @param load The amount of load to generate. The exact definition is left to the {@link LoadGenerator}.
     */
    public void iteration(LoadGenerator<?> loadGenerator, int load) {
        loadGenerator.generateLoad(load);
    }

    @Override
    public String toString() {
        return String.format("%s [Period: %dms, Deadline: %dms]", getClass().getSimpleName(),
                NANOSECONDS.toMillis(periodInNanos), NANOSECONDS.toMillis(periodInNanos));
    }

}
