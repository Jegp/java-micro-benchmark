/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

import benchmark.BenchmarkPrinter;
import benchmark.BenchmarkRunner;
import benchmark.LoadIterator;
import benchmark.RandomMemoryLoadGenerator;
import model.Task;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The CLI entry point for this benchmarking system. Below is a brief description of the framework. Please refer
 * to the <code>README</code> at the page on GitHub for more information:
 * <a href="https://github.com/Jegp/java-micro-benchmark">https://github.com/Jegp/java-micro-benchmark</a>
 * <h2>Benchmarking</h2>
 * <p>
 * Performance evaluation is an art, and this benchmarking framework is simply one attempt at extracting metrics
 * to compare certain environments to some metrics.
 * </p>
 * <h3>Metrics</h2>
 * <p>
 * This framework focuses specifically on real-time systems, which require a system to respond within a
 * reasonable delay. In this framework that metric is called the <i>response time</i>. The framework also
 * includes a way of measuring the total execution time for a task. In real-time systems
 * this criterion is important because a system can be under restraints of time. In this framework we call this
 * the <i>deadline</i>. Each task is executed with a certain delay, to illustrate that regardless of any failures
 * in meeting the <i>deadline</i>, the next task will start at a certain time. This is refered to as the
 * <i>period time</i>.
 * </p>
 * <h3>Workload</h3>
 * <p>
 * To put strain on the system, a {@link benchmark.LoadGenerator} generates a load. This load is defined by a
 * positive integer and will double for each iteration.
 * </p>
 * <h2>Benchmarking cases</h2>
 * <p>
 * This framework comes with three built-in use-cases: a slow cycle with a period time of 1200ms, a fast
 * cycle with a period time of 40 and a continuous cycle with a period time of 0.
 * </p>
 * <p>
 * The use cases are defined in the <code>tasks.yml</code> file. If provided with the <code>-f</code> flag,
 * the program can run any use case, defined in the file.
 * </p>
 * <h3>Runtime</h3>
 * <p>To somehow control how long the benchmarking should run, the <i>runtime</i> parameter lets you define
 * how long each benchmark iteration should run.
 * </p>
 * <h2>Collecting data</h2>
 * <p>
 * The data is collected in a folder named after the benchmarking case. The folder will be created in the current
 * directory and will contain files prefixed with the current workload. Aligned with the metrics, each iteration
 * generates three files for the response time (response), deadline (deadline) and period time (period).
 * </p>
 * <p>
 * Each file contains a High Dynamic Range (HDR) Histogram, which simply contains a histogram with the
 * measured times in different bins.
 * </p>
 *
 * @author jepeders
 * @see <a href="https://github.com/Jegp/java-micro-benchmark">https://github.com/Jegp/java-micro-benchmark</a>
 */
public class BenchmarkMain {

    private static final String FAST_CYCLE = "fast";
    private static final String SLOW_CYCLE = "slow";
    private static final String CONTINUOUS_CYCLE = "continuous";

    private static final long DEFAULT_RUNTIME_SECONDS = 900;
    private static final int DEFAULT_ITERATIONS = 8;
    private static final String DEFAULT_CASES_FILE = "tasks.yml";

    private static final String DEFAULT_CLASS_TEST = "benchmark.RandomMemoryLoadGenerator";

    private final BenchmarkPrinter printer;
    private final BenchmarkRunner runner;

    public BenchmarkMain(Task task, long runtimeInNanos, int iterations, String taskName, String classTestName,
            String uniqueClassName) {
        this(task, runtimeInNanos, iterations, new BenchmarkPrinter(uniqueClassName + "_" + taskName), classTestName);
    }

    public BenchmarkMain(Task task, long runtimeInNanos, int iterations, BenchmarkPrinter printer,
            String classTestName) {
        this.runner = new BenchmarkRunner(task, classTestName, runtimeInNanos, iterations);
        this.printer = printer;

        long totalRuntimeInSeconds = (iterations + 1 /* Including warmup */)
                * TimeUnit.NANOSECONDS.toSeconds(runtimeInNanos);
        String initMessage = String.format("Preparing %s benchmark for %d seconds with %d iteration(s)",
                task.getClass().getSimpleName(), totalRuntimeInSeconds, iterations);
        String expectedFinish = String.format("Expected time of completion: %s", java.time.LocalDateTime.now()
                .plusSeconds(totalRuntimeInSeconds).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        printer.getStandardOutput().println(initMessage);
        printer.getStandardOutput().println(expectedFinish);
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
        String classTestName = DEFAULT_CLASS_TEST;
        String casesFile = null;
        String taskName = null;

        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.equals("-i")) {
                    iterations = Integer.parseInt(args[++i]);
                } else if (arg.equals("-cl")) {

                    try {

                        classTestName = args[++i];

                        /** Verification that the class is working */
                        Class<?> classToTest = Class.forName(classTestName);
                        Object objectToTest = classToTest.newInstance();

                    } catch (Exception e) {
                        System.err.println("Error: Failed to load giving class ");
                        e.printStackTrace(System.err);
                        System.err.println("---------------------");

                    }

                } else if (arg.equals("-t")) {
                    runtimeInSeconds = Integer.parseInt(args[++i]);
                } else if (arg.equals("-f")) {
                    casesFile = args[++i];
                } else {
                    taskName = args[i];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: Missing parameter");
            System.err.println("---------------------");
            printHelp();
            System.exit(1);
        }

        if (taskName == null) {
            System.err.println("Error: No task given");
            System.err.println("---------------------");
            printHelp();
            System.exit(1);
        }

        try {
            InputStream fileStream = casesFile == null ? BenchmarkMain.class.getResourceAsStream(DEFAULT_CASES_FILE)
                    : new FileInputStream(casesFile);

            final Map<String, Task> tasks = CaseFileParser.parse(fileStream);
            if (tasks.containsKey(taskName)) {
                String uniqueClassName = classTestName.split("[.]")[1];
                return new BenchmarkMain(tasks.get(taskName), TimeUnit.SECONDS.toNanos(runtimeInSeconds), iterations,
                        taskName, classTestName, uniqueClassName);
            } else {
                System.err.println(String.format("Error: No task named %s in file %s", taskName, casesFile));
                System.err.println("Available task names: " + tasks.keySet());
                System.err.println("---------------------");
                printHelp();
                System.exit(3);
            }
        } catch (IOException e) {
            System.err.println("Error: Failed to parse case file");
            e.printStackTrace(System.err);
            System.err.println("---------------------");
            printHelp();
            System.exit(2);
        } catch (UnsupportedOperationException e) {
            System.err.println("Error: " + e);
            System.err.println("---------------------");
            printHelp();
            System.exit(4);
        }

        throw new RuntimeException();
    }

    private static void printHelp() {

        System.out.println("Benchmark");
        System.out.println("\tBenchmarks the period, deadline and response times of a Java application.");
        System.out.println("Usage:");
        System.out.println("\tBenchmark name (fast, slow, continuous) [-i iterations] [-t time] [-cl package.ClassToTest] [-f benchmark-cases]");
        System.out.println("");
        System.out.println("\tBuilt-in benchmarks:");
        System.out.println("\t  slow:       A cycle of 1.2 seconds, with a deadline of 0.7 seconds");
        System.out.println("\t  fast:       A cycle of 40 ms (25 hz)");
        System.out.println("\t  continuous: A continuous task that should get as fast response time as possible");
        System.out.println("");
        System.out.println("Options:");
        System.out.println(
                "\t-i iterations\tThe number of iterations to run with increasing load. Default: 8. Max value: 31");
        System.out.println("\t-t time\t\tSpecifies the runtime per iteration in seconds. Default: 900");
        System.out.println("\t-cl class\tA path to the class you want to run the benchmark on");
        System.out.println("\t-f cases\tA path to a YAML file, which defines the use-case to run");
        System.out.println("");
        System.out.println("Exit codes:");
        System.out.println("\t1\tNot enough parameters were given");
        System.out.println("\t2\tError parsing case YAML file");
        System.out.println("\t3\tCould not find requested task");
        System.out.println("\t4\tUnsupported parameter values");
        System.out.println("");
        System.out.println("About:");
        System.out.println("\tCopyright CERN (c) 2015");
        System.out.println("\tAuthor: Jens Egholm Pedersen <jensegholm@protonmail.com>");
    }

}

