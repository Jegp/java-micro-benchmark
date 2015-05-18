/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

public class SystemStatus {

    private final long loadedClasses;
    private final long unloadedClasses;

    public SystemStatus() {
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
        loadedClasses = classLoadingBean.getTotalLoadedClassCount();
        unloadedClasses = classLoadingBean.getUnloadedClassCount();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        /* @formatter:off */
        builder.append("System status:")
               .append(String.format("\n\tLoaded classes: %d", this.loadedClasses))
               .append(String.format("\n\tUnloaded classes: %d\n", this.unloadedClasses));
        /* @formatter:on */
        return builder.toString();
    }

    public String toString(SystemStatus that) {
        StringBuilder builder = new StringBuilder();
        /* @formatter:off */
        builder.append("System status comparison:")
               .append(String.format("\n\tLoaded classes diff: %d (%d/%d)", that.loadedClasses - this.loadedClasses, that.loadedClasses, this.loadedClasses))
               .append(String.format("\n\tUnloaded classes diff: %d (%d/%d)\n", that.unloadedClasses - this.unloadedClasses, that.unloadedClasses, this.unloadedClasses));
        /* @formatter:on */
        return builder.toString();
    }
}
