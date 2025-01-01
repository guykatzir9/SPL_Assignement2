package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> stampedDetectedObjectsList;
    private StampedDetectedObjects MostRecent;


    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectList) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.stampedDetectedObjectsList = detectedObjectList;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<StampedDetectedObjects> getStampedDetectedObjectsList() {
        return stampedDetectedObjectsList;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isUP () {
        return this.status == STATUS.UP;
    }

    /**
     * Retrieves the detected objects for a specific tick.
     *
     * @param tick The current tick.
     * @return The StampedDetectedObjects at the given tick, or null if no data is available.
     */
    public StampedDetectedObjects getObjectAtTick(int tick) {
        for (StampedDetectedObjects data : stampedDetectedObjectsList) {
            if (data.getTime() == tick) {
                return data;
            }
        }
        return null;
    }

}
