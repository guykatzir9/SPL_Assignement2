package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    private static final StatisticalFolder instance = new StatisticalFolder();
    private final AtomicInteger systemRuntine;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;

    private StatisticalFolder () {

        this.numDetectedObjects = new AtomicInteger(0);
        this.systemRuntine =    new AtomicInteger(0);
        this.numLandmarks =     new AtomicInteger(0);
        this.numTrackedObjects= new AtomicInteger(0);
    }

    public static StatisticalFolder getInstance() {
        return instance;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getSystemRuntine() {
        return systemRuntine.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void incrementNumDetectedObjects(int numDetectedObjects) {
        this.numDetectedObjects.addAndGet(numDetectedObjects);
    }

    public void incrementNumLandmarks(int numLandmarks) {
        this.numLandmarks.addAndGet(numLandmarks);
    }

    public void incrementNumTrackedObjects(int numTrackedObjects) {
        this.numTrackedObjects.addAndGet(numTrackedObjects);
    }

    public void incrementSystemRuntine(int systemRuntine) {
        this.systemRuntine.addAndGet(systemRuntine);
    }
}
