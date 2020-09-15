package lit.litfx.core.utils;

/**
 *
 * @author Birdasaur
 */
public enum Utils {
    INSTANCE;

    public static void printTotalTime(long startTime) {
        long estimatedTime = System.nanoTime() - startTime;
        long totalNanos = estimatedTime;
        long s = totalNanos / 1000000000;
        totalNanos -= s * 1000000000;
        long ms = totalNanos / 1000000;
        totalNanos -= ms * 1000000;
        long us = totalNanos / 1000;
        totalNanos -= us * 1000;
        System.out.println("Total elapsed time: Total ns: " + estimatedTime
                + ", " + s + ":s:" + ms + ":ms:" + us + ":us:" + totalNanos + ":ns");
    }        
}
