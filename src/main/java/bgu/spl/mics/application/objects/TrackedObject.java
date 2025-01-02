package bgu.spl.mics.application.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private final String id;
    private final int time;
    private final String description;
    private final List<CloudPoint> coordinates;
    private final int processedTime;


    public TrackedObject(String id, int time, String description, List<CloudPoint> coordinates , int processedTime) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
        this.processedTime = processedTime;

    }

    public String getDescription() {
        return description;
    }

    public int getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public int getProcessedTime() {
        return processedTime;
    }
}
