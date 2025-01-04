package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {

    private int currentTick;
    private STATUS status;
    private final List<Pose> PoseList = DataLoader.loadPoseData(Config.getPoseJsonFile());

    public GPSIMU () {
        this.status = STATUS.UP;
        this.currentTick = 0;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public STATUS getStatus() {
        return status;
    }

    public List<Pose> getPoseList() {
        return PoseList;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public Pose getCurrentPose() {
        for (Pose p : PoseList) {
            if (p.getTime() == currentTick) {
                return p;
            }
        }
        return null;
    }
}
