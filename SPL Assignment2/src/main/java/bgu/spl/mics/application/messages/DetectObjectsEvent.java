package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

/**
 * DetectObjectsEvent is an event sent by the camera service to request
 * LiDAR workers to process detected objects
 */

public class DetectObjectsEvent implements Event<List<DetectedObject>> {
    private final StampedDetectedObjects stampedDetectedObjects;
    private final int sendingTick;
    private final int DetectionTick;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects , int detectionTick , int sendingTick) {
        this.stampedDetectedObjects = detectedObjects;
        this.DetectionTick = detectionTick;
        this.sendingTick = sendingTick;

    }


    public StampedDetectedObjects getStampedDetectedObjects() {
        return this.stampedDetectedObjects;
    }

    public int getDetectionTick() {
        return DetectionTick;
    }

    public int getSendingTick() {
        return sendingTick;
    }
}
