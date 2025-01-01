package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * this event sent by a LiDAR worker to the fusion-SLAM
 * service with a request to process the Tracked objects for mapping
 */

public class TrackedObjectsEvent implements Event<Boolean> {
    private final List<TrackedObject> trackedObjects;
    private final int Tick;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, int Tick) {
        this.trackedObjects = trackedObjects;
        this.Tick = Tick;
    }

    public List<TrackedObject> getTrackedObjects () {
        return this.trackedObjects;
    }

    public int getTick() {
        return Tick;
    }
}
