package model;

/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

/**
 * A period in time, denoted by a start and end time.
 * 
 * @author jepeders
 */
public class Period {

    public final long startTime;
    public final long endTime;

    /**
     * Creates a period with a beginning and an ending.
     * 
     * @param startTime The start of the period
     * @param endTime The end of the period
     */
    public Period(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getPeriod() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return startTime + " -> " + endTime + " = " + (endTime - startTime);
    }

}