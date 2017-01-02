/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package model;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import benchmark.LoadGenerator;
import benchmark.LoadIterator;

/**
 * A task that can run an indefinite number of iterations.
 * 
 * @author jepeders
 */
public abstract class Task {

    public final long periodInNanos;
    public final long deadlineInNanos;
    private final int initialWorkload;

    /**
     * Creates a task with a deadline and period in the given unit.
     * 
     * @param deadlineInNanos The maximum time it can take to perform one {@link #iteration()}.
     * @param periodInNanos The time interval between the beginning of iterations.
     */
    public Task(long deadlineInNanos, long periodInNanos, int initialWorkload) {
        this.deadlineInNanos = deadlineInNanos;
        this.periodInNanos = periodInNanos;
        this.initialWorkload = initialWorkload;
    }

    public LoadIterator getLoadIterator(int iterations) {
        return LoadIterator.ofBoundAndSize(initialWorkload, iterations);
    }

    /**
     * An iteration of the task that uses the {@link #loadGenerator} given in the constructor to generate an amount of
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
