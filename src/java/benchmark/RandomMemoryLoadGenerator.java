/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */
package benchmark;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A class that can generate synthetic load and whose operations are not being optimised away by compile or runtime
 * environment. The results of the class uses the {@link Random} utility with a constant seed, so the results are
 * idempotent.
 * 
 * @author jepeders
 */
public class RandomMemoryLoadGenerator implements LoadGenerator<Object> {

    private static final long SEED = 1843710252; /* Random number */
    private static final int UPPER_LIMIT = 65536; /* 1 << 16 */

    private Random random;

    private LinkedList<Object> young;
    private LinkedList<Object> old;

    /**
     * Generates a load by storing memory
     * 
     * @param load The amount of times the load generator should iterate the same task.
     * @return
     * @throws IllegalArgumentException if the load is larger than 262144 (1 << 18)
     */
    @Override
    public List<Object> generateLoad(int load) {
        if (load > UPPER_LIMIT) {
            throw new IllegalArgumentException("Load cannot be higher than " + UPPER_LIMIT);
        }
        /* Always use the same seed per load */
        random = new Random(SEED);

        /* Create a linked list to use random memory cells */
        old = new LinkedList<Object>();

        for (int i = 0; i < load; i++) {
            /* (Re-)initialise the young list */
            young = new LinkedList<Object>();

            /* Creates a number of objects of a random size */
            int size = random.nextInt(UPPER_LIMIT);
            young.add(generateObjectWithArrayOfSize(size));

            /* 25% of the times we move one random element from young to old to simulation fractioning */
            if (random.nextBoolean() && random.nextBoolean() && !young.isEmpty()) {
                Object removed = young.remove(random.nextInt(young.size()));
                if (old.size() < UPPER_LIMIT) {
                    old.add(removed);
                }

                /* 12.5% of the times we remove one random element from old to simulate fractioning */
                if (random.nextBoolean() && !old.isEmpty()) {
                    old.remove(random.nextInt(old.size()));
                }
            }
        }
        return young;
    }

    public List<Object> dummyMethodGetOld() {
        return old;
    }

    public List<Object> dummyMethodGetYoung() {
        return young;
    }

    /**
     * Generates a single custom object containing an array of the given size filled with random data.
     * 
     * @param size The size of the array to create.
     * @return An {@link Object} containing an array.
     */
    private Object generateObjectWithArrayOfSize(int size) {
        /* Create a double array */
        final double tmp[] = new double[size];
        Arrays.fill(tmp, random.nextDouble());

        /* Return an object containing the doubles to avoid it being optimised away */
        return new Object() {
            @SuppressWarnings("unused")
            public double xs[] = tmp;
        };
    }

}
