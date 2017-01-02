/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import model.Task;
import benchmark.BenchmarkPrinter;
import benchmark.BenchmarkRunner;
import benchmark.RandomMemoryLoadGenerator;
import cases.ContinuousCycle;
import cases.FastCycle;
import cases.SlowCycle;

/**
 * The CLI entry point for this benchmarking system.
 * 
 * @author jepeders
 */
public class BenchmarkMain {

    private static final String FAST_CYCLE = "fast";
    private static final String SLOW_CYCLE = "slow";
    private static final String CONTINUOUS_CYCLE = "continuous";

    public static final long DEFAULT_RUNTIME_SECONDS = 900;
    public static final int DEFAULT_ITERATIONS = 8;

    private final BenchmarkPrinter printer;
    private final BenchmarkRunner runner;

    public BenchmarkMain(Task task, long runtimeInNanos, int iterations) {
        this(task, runtimeInNanos, iterations, new BenchmarkPrinter(task.getClass().getSimpleName()));
    }

    public BenchmarkMain(Task task, long runtimeInNanos, int iterations, BenchmarkPrinter printer) {
        this.runner = new BenchmarkRunner(task, new RandomMemoryLoadGenerator(), runtimeInNanos, iterations);
        this.printer = printer;

        long totalRuntimeInSeconds = (iterations + 1 /* Including warmup */)
                * TimeUnit.NANOSECONDS.toMillis(runtimeInNanos);
        String initMessage = String.format("Preparing %s benchmark for %d seconds with %d iteration(s)", task
                .getClass().getSimpleName(), totalRuntimeInSeconds, iterations);
        printer.getStandardOutput().println(initMessage);
    }

    public void run() {
        PrintStream output = printer.getStandardOutput();
        output.println("Benchmark starting");
        output.println("------------------");

        /* Stage 1: Warmup */
        output.println("Stage 1: Warming up");
        runner.warmup(printer);

        /* Stage 2: Clean up from stage 2 */
        output.println("Stage 2: Cleanup");
        Runtime runtime = Runtime.getRuntime();
        runtime.runFinalization();
        runtime.gc();

        /* Stage 3: Run! */
        output.println("Stage 3: Benchmarking");
        runner.run(printer);
        output.println("------------------");
        output.println("Benchmark completed successfully");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            System.exit(1);
            return;
        }
        parseArgs(args).run();
    }

    private static BenchmarkMain parseArgs(String[] args) {
        long runtimeInSeconds = DEFAULT_RUNTIME_SECONDS;
        int iterations = DEFAULT_ITERATIONS;
        Task task = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-i")) {
                iterations = Integer.parseInt(args[++i]);
            } else if (arg.equals("-t")) {
                runtimeInSeconds = Integer.parseInt(args[++i]);
            } else if (arg.equals(FAST_CYCLE)) {
                task = new FastCycle();
            } else if (arg.equals(SLOW_CYCLE)) {
                task = new SlowCycle();
            } else if (arg.equals(CONTINUOUS_CYCLE)) {
                task = new ContinuousCycle();
            }
        }
        if (task == null) {
            System.out.println("Error: No task given.");
            System.out.println("---------------------");
            printHelp();
            System.exit(1);
        }

        return new BenchmarkMain(task, TimeUnit.SECONDS.toNanos(runtimeInSeconds), iterations);
    }

    private static void printHelp() {
        System.out.println("Benchmark");
        System.out.println("\tBenchmarks the period, deadline and response times of three use cases:");
        System.out.println("\t  slow:       A cycle of 1.2 seconds, with a deadline of 0.7 seconds");
        System.out.println("\t  fast:       A cycle of 40 ms (25 hz)");
        System.out.println("\t  continuous: A continuous task that should get as fast response time as possible");
        System.out.println("");
        System.out.println("Usage:");
        System.out.println("\tBenchmark name (fast, slow, continuous) [-i iterations] [-t time]");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("\t-i iterations\tThe number of iterations to run with increasing load");
        System.out.println("\t-t time\t\tSpecifies the runtime per iteration in seconds");
        System.out.println("");
        System.out.println("Exit codes:");
        System.out.println("\t1\tNot enough parameters were given");
        System.out.println("");
        System.out.println("About:");
        System.out.println("\tCopyright CERN (c) 2015");
        System.out.println("\tAuthor: Jens Egholm Pedersen <jens.egholm@cern.ch>");
    }

}
