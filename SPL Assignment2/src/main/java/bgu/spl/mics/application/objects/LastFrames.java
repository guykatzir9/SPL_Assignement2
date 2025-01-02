package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LastFrames {

    private static final LastFrames instance = new LastFrames();
    private final Map<String , StampedDetectedObjects> cameras;
    private final Map<String , List<TrackedObject>> LiDars;

    private LastFrames (){
        this.cameras = new HashMap<>();
        this.LiDars = new HashMap<>();
    }

    public static LastFrames getInstance() {
        return instance;
    }

    public Map<String, List<TrackedObject>> getLiDars() {
        return LiDars;
    }

    public Map<String, StampedDetectedObjects> getCameras() {
        return cameras;
    }

    public void setCameras(String string , StampedDetectedObjects SDO) {
        if (cameras.containsKey(string))
            cameras.replace(string , SDO );
        else {
            cameras.put(string, SDO);
        }
    }

    public void setLiDars(String LidarId , List<TrackedObject> list) {
        // check if this LidarId is already in the map
        if (LiDars.containsKey(LidarId))
            LiDars.replace(LidarId , list);

        else {
            LiDars.put(LidarId, list);
        }
    }
}
