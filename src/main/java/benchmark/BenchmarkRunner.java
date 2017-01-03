package benchmark;

/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

import java.io.PrintStream;

import model.Task;
import model.TaskIteration;

/**
 * A class that can run a benchmark for a task.
 * 
 * @author jepeders
 */
public class BenchmarkRunner {

    public final long runtimeInNanos;
    public final TaskRunner taskRunner;
    private final LoadIterator loadIteratorPrototype;
    private final int iterations;

    public BenchmarkRunner(Task task, LoadGenerator<?> generator, long runtimeInNanos, int loadIterations) {
        this.runtimeInNanos = runtimeInNanos;
        this.taskRunner = new TaskRunner(task, generator, runtimeInNanos);
        this.iterations = loadIterations;
        this.loadIteratorPrototype = task.getLoadIterator(loadIterations);
    }

    public void run(BenchmarkPrinter printer) {
        PrintStream writer = printer.getStandardOutput();
        int index = 0;
        LoadIterator loadIterator = loadIteratorPrototype.copy();

        while (loadIterator.hasNext()) {
            int load = loadIterator.next();
            writer.println(String.format("\tRunning iteration %d of %d with %d load", ++index, iterations, load));
            TaskIteration iteration = taskRunner.run(load);
            iteration.write(printer);
        }
    }

    public long getRuntimeInNanos() {
        return runtimeInNanos;
    }

    public void warmup(BenchmarkPrinter printer) {
        PrintStream writer = printer.getStandardOutput();

        writer.println("Warming up ");
        long durationInNanos = taskRunner.warmup(1);
        taskRunner.run(0); /* Loads the classes in the run method */
        writer.println(" ... completed in " + durationInNanos + "ns\n");

        writer.print("Warmup completed: " + new SystemMetrics().getMetrics());
    }

}
