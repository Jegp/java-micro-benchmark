/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A iterator that represents of a number of work done on a system. For every step the work doubles until a certain
 * limit. This class is not meant for concurrent operations and is not thread safe.
 * 
 * @author jepeders
 */
public class LoadIterator implements Iterator<Integer> {

    private final int baseLoad;
    private final int maxIterations;
    private int currentIteration;

    private LoadIterator(int baseLoad, int maxIterations) {
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Cannot create an iterator with less than 1 iterations.");
        }

        this.baseLoad = baseLoad;
        this.maxIterations = maxIterations;
        this.currentIteration = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIteration < maxIterations;
    }

    @Override
    public Integer next() {
        if (currentIteration >= maxIterations) {
            throw new NoSuchElementException("Empty iterator");
        }
        return baseLoad << (currentIteration++);
    }

    public LoadIterator copy() {
        return new LoadIterator(baseLoad, maxIterations);
    }

    private static void testUpperIterationBound(int base, int power) throws UnsupportedOperationException {
        final int shift = power - 1; /* Subtract 1 to skip first iteration */
        if (Integer.numberOfLeadingZeros(base) - shift < 0) {
            throw new UnsupportedOperationException(String.format(
                    "Cannot create an iterator shifted %d times. Ceiling is %d", shift,
                    Integer.bitCount(Integer.MAX_VALUE)));
        }
    }

    /**
     * Creates an iterator which starts with the given bound and doubles a number of times defined by the size of the
     * iterator.
     * 
     * @param lowerBound The workload to start with.
     * @param iteratorSize The number of times the workload should double.
     * @return A {@link LoadIterator}.
     * @throws IllegalArgumentException If the lower bound is less than 1.
     */
    public static LoadIterator ofBoundAndSize(int lowerBound, int iteratorSize) {
        if (lowerBound < 1) {
            throw new IllegalArgumentException("Cannot start with a lower bound of less than 1.");
        }
        testUpperIterationBound(lowerBound, iteratorSize);
        return new LoadIterator(lowerBound, iteratorSize);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from this iterator");
    }

}
