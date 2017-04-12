/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

/**
 * Metrics which can be exported to a string and compared to another metric.
 *
 * @param <T> The metrics type to compare to.
 */
interface Metrics<T extends Metrics> {

    String getMetrics();

    String compareMetrics(T Metrics);

}
