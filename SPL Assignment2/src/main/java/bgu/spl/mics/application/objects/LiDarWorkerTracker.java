package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    private final Map<Integer, List<List<TrackedObject>>> TrackObjectsMap;
    private int lastSentTick;
    private int lastDetectionTick;
    private final LiDarDataBase liDarDataBase = LiDarDataBase.getInstance("example_input_2/lidar_data.json");


    public LiDarWorkerTracker(int id, int freq) {
        this.id = id;
        this.frequency = freq;
        this.status = STATUS.UP;
        this.lastTrackedObjects = null;
        this.lastSentTick = -1;
        this.lastDetectionTick = -1;
        this.TrackObjectsMap = new HashMap<>();

    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isUp() {
        return this.status == STATUS.UP;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public int getLastSentTick() {
        return lastSentTick;
    }

    public int getLastDetectionTick() {
        return lastDetectionTick;
    }

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }

    public void setLastDetectionTick(int lastDetectionTick) {
        this.lastDetectionTick = lastDetectionTick;
    }

    public void setLastSentTick(int lastSentTick) {
        this.lastSentTick = lastSentTick;
    }

    public Map<Integer, List<List<TrackedObject>>> getTrackObjectsMap() {
        return TrackObjectsMap;
    }

    public void addToMap(int time, List<TrackedObject> list) {

        if (!TrackObjectsMap.containsKey(time)) {
            List<List<TrackedObject>> TempList = new ArrayList<>();
            TempList.add(list);
            this.TrackObjectsMap.put(time, TempList);
        }
        else {
            this.TrackObjectsMap.get(time).add(list);
        }
    }

    public List<TrackedObject> processDetectedObjectsEvent(DetectObjectsEvent event) {

        StampedDetectedObjects stampedDetectedObjects = event.getStampedDetectedObjects();
        List<TrackedObject> output = new ArrayList<>();
        for (DetectedObject DO : stampedDetectedObjects.getDetectedObjects()) {
            String id = DO.getId();
            int Tick = event.getDetectionTick();
            int processTime = event.getSendingTick();
            String desc = DO.getDescription();
            List<CloudPoint> cloudPoints = liDarDataBase.getCloudPoints(id, Tick);
            output.add(new TrackedObject(id, Tick, desc, cloudPoints,processTime));
        }
        return output;
    }

}
