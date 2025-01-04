package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

/**
 * DetectObjectsEvent is an event sent by the camera service to request
 * LiDAR workers to process detected objects
 */

public class DetectObjectsEvent implements Event<Boolean> {
    private final StampedDetectedObjects stampedDetectedObjects;
    private final int sendingTick;
    private final int DetectionTick;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects , int detectionTick , int sendingTick, String sender) {
        this.stampedDetectedObjects = detectedObjects;
        this.DetectionTick = detectionTick;
        this.sendingTick = sendingTick;
        System.out.println(sender + " Detected objects at tick :" + this.DetectionTick + " Detected objects: " + this.stampedDetectedObjects.getDetectedObjects());
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
