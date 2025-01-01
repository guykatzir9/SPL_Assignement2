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

    public void setLiDars(String string , List<TrackedObject> list) {
        if (LiDars.containsKey(string))
            LiDars.replace(string , list);

        else {
            LiDars.put(string, list);
        }
    }






}
