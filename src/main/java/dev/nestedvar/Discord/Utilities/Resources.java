package dev.nestedvar.Discord.Utilities;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class Resources {
    public double getCPULoad() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            return osBean.getSystemLoadAverage();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getRAMUsage() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            return String.valueOf((allocatedMemory - freeMemory) / 1000 / 1000);
        } catch (Exception e) {
            return "There was an error while attempting to collect ram usage!";
        }
    }
}
