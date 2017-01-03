/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import java.util.List;

/**
 * A load generator that can generate an arbitrary load. The generator returns a list of the 'load' objects to prevent
 * the objects from being optimised away during just-in-time compilation.
 * 
 * @author jepeders
 * @param <T> The type of objects to return.
 */
public interface LoadGenerator<T> {

    /**
     * Generates a certain amount of load. Incremented by a factor of two each time (1, 2, 4, 8 ...).
     * 
     * @param load The amount of load to generate.
     * @return A list of the objects generated by this load generator. The list is returned to avoid just-in-time
     *         compilation optimisation.
     */
    List<T> generateLoad(int load);

}