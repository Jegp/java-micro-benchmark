/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package model;

import java.io.PrintStream;

import org.HdrHistogram.Histogram;

import benchmark.BenchmarkPrinter;
import benchmark.SystemMetrics;

/**
 * The results of one run of a {@link Task} that can be dumped to a {@link PrintStream}.
 * 
 * @author jepeders
 */
public class TaskIteration {

    public final int load;

    private final Histogram hiccupHistogram;
    private final Histogram deadlineHistogram;
    private final Histogram periodHistogram;

    private final SystemMetrics statusAfter;
    private final SystemMetrics statusBefore;

    public TaskIteration(int load, SystemMetrics statusBefore, SystemMetrics statusAfter, Histogram deadlineHistogram,
                         Histogram periodHistogram, Histogram hiccupHistogram) {
        this.load = load;
        this.hiccupHistogram = hiccupHistogram;
        this.periodHistogram = periodHistogram;
        this.deadlineHistogram = deadlineHistogram;
        this.statusBefore = statusBefore;
        this.statusAfter = statusAfter;
    }

    public void write(BenchmarkPrinter printer) {
        writeHeader(printer.getStandardOutput());
        writeHistogram(hiccupHistogram, printer.getHiccupOutput(load));
        writeHistogram(deadlineHistogram, printer.getDeadlineOutput(load));
        writeHistogram(periodHistogram, printer.getPeriodOutput(load));
    }

    private void writeHeader(PrintStream output) {
        output.println(String.format("Task iteration with %d load", load));
        output.println(statusBefore.compareMetrics(statusAfter));
    }

    private void writeHistogram(Histogram histogram, PrintStream output) {
        histogram.outputPercentileDistribution(output, 1d);
    }

}
