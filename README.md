# java-micro-benchmark
A benchmarking tool in Java with nano-second precision developed for my bachelor thesis on 
firm real-time Java at the European Center for Nuclear Research
([CERN](http://home.cern/)).

Be advised that micro benchmarks are NOT advised. The results of running this benchmark 
might not be informative. Benchmarking is comparable to an artform and is onerous
to get right. The aim of this framework is to give an estimation of a real-time behaviour
which is as close as possible to running the real thing.

## Usage
The project is set up to run with [Gradle](http://gradle.org).
The easiest way is to use this project is to install Gradle, clone the project,
enter the directory and execute the following:

    gradle jar
    
This will give you a runnable ``.jar`` in the ``build/libs/`` directory. To test it out

    java -jar build/libs/java-micro-benchmark-0.8.jar
    
This will print some help on running the application. As a minimum the framework requires
the name of the benchmarking task to run. The three built-in options (slow, fast,
continuous) are described below.

Further, you can define the number of iterations to run with the ``-i`` flag.
For each iteration, the workload (defined in the benchmarking tasks) doubles.

The ``-t`` flag defines how long each iteration will run in seconds.
The default is 900 seconds. Including one iteration of warm-up, the total runtime of the
benchmark will be approximately ``iterations * runtime + runtime``.

The ``-f`` flag lets you define your own benchmarking tasks in a [YAML](http://yaml.org)
configuration file.

### Output
The results of the benchmark will be outputted to a folder in your current working
directory. It will be suffixed with a timestamp followed by the name of the benchmarking
task.

In the output folder a ``log`` will keep track of the benchmark progress. 

### Examples
Running the ``fast`` benchmark with 1 iteration at 10 seconds:

    java -jar java-micro-benchmark-0.8.jar fast -i 1 -t 10
    
Running the custom defined benchmark task ``mytask`` from the ``mytasks.yml`` 
YAML configuration file with 5 iterations and the default running time per iteration
of 900 seconds (this will take around 1 hour and 30 minutes including warm-up):

    java -jar java-micro-benchmark-0.8.jar mytask -f mytasks.yml -i 5
    
Running the benchmark using the ``G1`` garbage collector:

    java -XX:+UseG1GC -jar java-micro-benchmark-0.8.jar fast -i 1

## Benchmarking metrics
The benchmarks are designed to be run in a system with time constraints. The framework
run ``Tasks`` which are iterations of *some* work.
Each iteration are evaluated on three criteria: how long it takes to complete (deadline),
how often a new iteration should be started (period) and how long it takes for the
VM to respond (response). These three metrics are output as
[dynamic range histograms](http://www.hdrhistogram.org/). Once per metric per iteration. So
three files in total per iteration.

### Workload
The framework currently simulates *work* by simply creating and removing 
elements from a lists at random (in the ``RandomMemoryLoadGenerator`` class).
I used this to test garbage collection algorithms, but it might not be suitable for
your use-case. To implement a more suitable algorithm, the interface ``LoadGenerator``
can be extended to implement some load, given a positive integer.
 
**Please note** that some Java compilers have aggressive optimisation for dead code.
Similarly, the JIT compiler can 

### Stages
The benchmarking runs in three stages: warm-up, cleanup and benchmarking. The warm-up is 
essentially one benchmark iteration and is done to avoid any JIT compilation or 
class-loading, which could impact the results. The ``log`` file the benchmark output
folder shows continuous statistics on how long the benchmark took along with
the number of classes loaded and unloaded. If there is a difference between this
number it means that the VM spent unnecessary time (or at least not purely
related to throughput performance) on class-loading. 

## Benchmark tasks
Benchmarking is a complicated endeavour and this benchmark focuses on semi real-time
tasks, I've included three benchmarking tasks:

| Benchmark task  | Period time | Deadline | Initial workload |
| --------------- | ----------- | -------- | ---------------- |
| Slow            | 1200ms      | 1700ms   | 1024             |
| Fast            | 40ms        | 10ms     | 16               |
| Continuous      | 0ms         | 0ms      | 1                |

The tasks are defined in the ``tasks.yml`` file which can be found in
``src/main/java/resources/tasks.yml``. By using the ``-f`` flag you can point to your own
[YAML](http://yaml.org/) file.

## Validity
The results of this framework should not be expected to be transferred to a life-size
scenario. There are a plentitude of factors influencing the results of the frameworks. 
The only way to guarantee validity is to experiment with real-life code.

Especially three factors make the results hard to transfer

1. Synthetic workload. The framework simulates a piece of work which is nowhere near the
real thing. To mediate this, implement your own ``LoadGenerator``.
2. Benchmarking environment. 

## Benchmarking details

This framework is based on my thesis written at the
[IT University of Copenhagen](http://itu.dk). I cannot publish the thesis due to
confidential material, but you are welcome to contact me if you have any questions

## Thanks to
My supervisor at CERN, Vito Baggiolini deserves a big part of the credit for starting
this project and keeping me on track. My supervisor at the IT University of Copenhagen
Peter Sestoft, was a great source of inspiration both with regards to finding literature
and providing valuable critique on the project.

The project depends on the HdrHistogram recording tool and is in general heavily
inspired by the jHiccup library by Gil Tene.


Copyright @ CERN 2015
