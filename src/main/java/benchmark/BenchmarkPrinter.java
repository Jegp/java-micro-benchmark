package benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

public class BenchmarkPrinter {

    public static final File DEFAULT_OUTPUT_FOLDER = new File(String.format("%1$tY%1$tm%1$td-%1$tT",
            Calendar.getInstance()));

    private static final String HICCUP_OUTPUT_FORMAT = "%d_response";
    private static final String DEADLINE_OUTPUT_FORMAT = "%d_deadline";
    private static final String PERIOD_OUTPUT_FORMAT = "%d_period";
    private static final String LOG_OUTPUT_NAME = "log";

    private final File folder;
    private final PrintStream logPrinter;

    public BenchmarkPrinter(String prefix) {
        this(new File(prefix + DEFAULT_OUTPUT_FOLDER));
    }

    public BenchmarkPrinter(File parentFolder) {
        this.folder = parentFolder;
        if (!parentFolder.isDirectory()) {
            if (!parentFolder.mkdir()) {
                throw new IllegalArgumentException("Failed to create folder");
            }
        }

        logPrinter = getPrintStreamForFileName(LOG_OUTPUT_NAME);
    }

    public PrintStream getStandardOutput() {
        return logPrinter;
    }

    public PrintStream getDeadlineOutput(int load) {
        return getOutputFromFormat(DEADLINE_OUTPUT_FORMAT, load);
    }

    public PrintStream getHiccupOutput(int load) {
        return getOutputFromFormat(HICCUP_OUTPUT_FORMAT, load);
    }

    private PrintStream getOutputFromFormat(String format, int load) {
        return getPrintStreamForFileName(String.format(format, load));
    }

    public PrintStream getPeriodOutput(int load) {
        return getOutputFromFormat(PERIOD_OUTPUT_FORMAT, load);
    }

    private PrintStream getPrintStreamForFileName(String name) {
        File file = new File(folder, name);
        try {
            return new PrintStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static BenchmarkPrinter ofTemp() {
        try {
            File tmpFile;
            tmpFile = File.createTempFile("benchmark", ".log");
            tmpFile.delete();
            tmpFile.mkdir();

            return new BenchmarkPrinter(tmpFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
