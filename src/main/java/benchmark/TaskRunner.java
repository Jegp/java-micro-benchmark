/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import model.Task;
import model.TaskIteration;

import org.HdrHistogram.Histogram;

/**
 * A class that can run a task and record the time of each run in a {@link Histogram} object. This runner will also
 * 'warm up' the VM to make sure that all JIT and other run-time optimisations have been performed <i>before</i> the
 * actual benchmarking.
 *
 * @author jepeders
 */
public class TaskRunner {

    private final Task task;
    private final long runningTimeInNanos;
    private Class<?> classToTest ;
    private Object objectForTest;

    /**
     * Creates a TaskRunner with a task that runs for a given duration in total.
     *
     * @param task           The task to iterate over many times.
     * @param generator      The {@link LoadGenerator} to create the load.
     * @param runtimeInNanos The total runtime of the task.
     */
    public TaskRunner(Task task, Class<?> classTotest, Object objectToTest, long runtimeInNanos) {
        this.task = task;
        this.runningTimeInNanos = runtimeInNanos;
        this.objectForTest = objectToTest;
        this.classToTest = classTotest;
        
    }

    /**
     * Runs the task in the given runtime with a given load.
     *
     * @param load The amount of load (stress) to generate
     * @return A {@link Histogram} object containing the recorded runtimes.
     */
    public final TaskIteration run(int load) {
        final SystemMetrics statusBefore = new SystemMetrics();
        final HiccupRecorder hiccupRecorder = new HiccupRecorder(true);
        final MetricRecorder deadlineRecorder = new MetricRecorder();
        final MetricRecorder periodRecorder = new MetricRecorder();
        final long stopTimeInNanos = System.nanoTime() + runningTimeInNanos;

        long iterationStopTimeInNanos = -1;
        long deadlineStartInNanos = -1;
        long deadlineDeltaInNanos = -1;
        long lastPeriodStartTime = -1;

        hiccupRecorder.start();

        
        while (System.nanoTime() < stopTimeInNanos) {
            
            iterationStopTimeInNanos = System.nanoTime() + task.periodInNanos;
            while (System.nanoTime() < iterationStopTimeInNanos) {

                /* Run the iteration and time it */
                deadlineStartInNanos = System.nanoTime();
                task.iteration(classToTest, objectForTest, load);
                deadlineDeltaInNanos = System.nanoTime() - deadlineStartInNanos;

                /* Record the deadline */
                deadlineRecorder.record(deadlineDeltaInNanos, task.deadlineInNanos);

                /* Record the period */
                if (lastPeriodStartTime > 0) {
                    periodRecorder.record(deadlineStartInNanos - lastPeriodStartTime, task.periodInNanos);
                }
                lastPeriodStartTime = deadlineStartInNanos;
            }
            
        }

        Histogram hiccupHistogram = hiccupRecorder.terminate();
        SystemMetrics statusAfter = new SystemMetrics();

        return new TaskIteration(load, statusBefore, statusAfter, deadlineRecorder.getHistogram(),
                periodRecorder.getHistogram(), hiccupHistogram);
    }

    /**
     * Warms up the JVM by running the given task using the given load
     *
     * @param load The load to give to the task
     * @return The period of the run in milliseconds.
     * @throws InterruptedException If the run was interrupted.
     */
    public long warmup(int load) {
        final long startTimeInNanos = System.nanoTime();
        run(load);
        final long endTimeNanos = System.nanoTime();
        return endTimeNanos - startTimeInNanos;
        
    }
}
