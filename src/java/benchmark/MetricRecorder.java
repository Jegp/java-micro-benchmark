/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.SingleWriterRecorder;

/**
 * An recorder that can store information about a run in constant time O(1). The class is <b>not</b> meant for
 * concurrent operations and is not thread-safe.
 */
public class MetricRecorder {

    private final SingleWriterRecorder recorder = new SingleWriterRecorder(1);

    /**
     * Stores an event in constant time.
     * 
     * @param event The event to record.
     * @param expectedInterval The expected interval to record.
     */
    public void record(long event, long expectedInterval) {
        recorder.recordValueWithExpectedInterval(event, expectedInterval);
    }
    
    public Histogram getHistogram() {
        return recorder.getIntervalHistogram();
    }
}
