/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package benchmark;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;

public class SystemMetrics implements Metrics<SystemMetrics> {

    private final GcMetrics gcMetrics = GcMetrics.create();
    private final ClassLoadingMetrics classMetrics = ClassLoadingMetrics.create();
    private final MemoryMetrics memoryMetrics = new MemoryMetrics();

    @Override
    public String getMetrics() {
        return "\nSystem statistics:\n" + classMetrics.getMetrics() +
                gcMetrics.getMetrics() + memoryMetrics.getMetrics();
    }

    @Override
    public String compareMetrics(SystemMetrics that) {
        return "System statistics comparison:\n" + classMetrics.compareMetrics(that.classMetrics) +
                gcMetrics.compareMetrics(that.gcMetrics) + memoryMetrics.compareMetrics(that.memoryMetrics);
    }

    private static final class ClassLoadingMetrics implements Metrics<ClassLoadingMetrics> {
        private final long loadedClasses;
        private final long unloadedClasses;

        private ClassLoadingMetrics() {
            ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
            loadedClasses = classLoadingBean.getTotalLoadedClassCount();
            unloadedClasses = classLoadingBean.getUnloadedClassCount();
        }

        static ClassLoadingMetrics create() {
            return new ClassLoadingMetrics();
        }

        @Override
        public String getMetrics() {
            return "Class loading statistics:\n" +
                    String.format("\tLoaded classes: %d\n", loadedClasses) +
                    String.format("\tUnloaded classes: %d\n", unloadedClasses);
        }

        @Override
        public String compareMetrics(ClassLoadingMetrics that) {
            return "Class loading statistics comparison:\n" +
                    String.format("\tLoaded classes diff: %d (%d/%d)\n", that.loadedClasses - this.loadedClasses, that.loadedClasses, this.loadedClasses) +
                    String.format("\tUnloaded classes diff: %d (%d/%d)\n", that.unloadedClasses - this.unloadedClasses, that.unloadedClasses, this.unloadedClasses);
        }
    }

    private static final class GcMetrics implements Metrics<GcMetrics> {

        final List<MemoryManagerMetrics> metrics;

        private GcMetrics(List<MemoryManagerMetrics> metrics) {
            this.metrics = metrics;
        }

        static GcMetrics create() {
            return new GcMetrics(ManagementFactory.getGarbageCollectorMXBeans().stream().map(bean ->
                    new MemoryManagerMetrics(bean.getObjectName().getCanonicalName(),
                            Math.max(0, bean.getCollectionCount()),
                            Math.max(0, bean.getCollectionTime()))).collect(Collectors.toList()));
        }

        @Override
        public String getMetrics() {
            return "Garbage collector statistics:\n" +
                    metrics.stream().map(metrics ->
                            String.format("\t%s:\n", metrics.name) +
                                    String.format("\t\tNumber of garbage collections:   %d\n", metrics.count) +
                                    String.format("\t\tCollection time in milliseconds: %d\n", metrics.time))
                            .collect(Collectors.joining());
        }

        @Override
        public String compareMetrics(GcMetrics that) {
            return "";
        }

        private static final class MemoryManagerMetrics {

            final String name;
            final long count;
            final long time;

            private MemoryManagerMetrics(String name, long count, long time) {
                this.name = name;
                this.count = count;
                this.time = time;
            }
        }

    }

    private static final class MemoryMetrics implements Metrics<MemoryMetrics> {

        final long maxMemory = Runtime.getRuntime().maxMemory();
        final long freeMemory = Runtime.getRuntime().freeMemory();
        final long usedMemory = Runtime.getRuntime().totalMemory();

        @Override
        public String getMetrics() {
            return "Memory statistics (in bytes):" +
                    "\n\tFree memory:  " + freeMemory +
                    "\n\tUsed memory:  " + usedMemory +
                    "\n\tTotal memory: " + maxMemory + "\n";
        }

        @Override
        public String compareMetrics(MemoryMetrics that) {
            return "Memory statistics comparison:\n" +
                    String.format("\tFree memory diff:  %d (%d/%d)\n", that.freeMemory - this.freeMemory, that.freeMemory, this.freeMemory) +
                    String.format("\tUsed memory diff:  %d (%d/%d)\n", that.usedMemory - this.usedMemory, that.usedMemory, this.usedMemory) +
                    String.format("\tTotal memory diff: %d (%d/%d)\n", that.maxMemory - this.maxMemory, that.maxMemory, this.maxMemory);
        }
    }

}
