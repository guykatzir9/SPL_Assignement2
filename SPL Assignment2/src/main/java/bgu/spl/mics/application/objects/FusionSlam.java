package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {

    private final List<LandMark> landMarks;
    private final List<Pose> Poses;
    private STATUS status;

    private FusionSlam() {
        this.landMarks = new ArrayList<>();
        this.Poses = new ArrayList<>();
        this.status = STATUS.UP;
    }

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public List<LandMark> getLandMarks() {
        return landMarks;
    }

    public List<Pose> getPoses() {
        return Poses;
    }

    public void updatePose(Pose newPose) {
        Poses.add(newPose);
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void updateLandmarks(List<LandMark> newLandMarks) {
        for (LandMark newLandMark : newLandMarks) {
            boolean updated = false;
            for (LandMark existingLandMark : landMarks) {
                if (existingLandMark.getId().equals(newLandMark.getId())) {
                    existingLandMark.refineLocation(newLandMark.getCoordinates());
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                landMarks.add(newLandMark);
                StatisticalFolder.getInstance().incrementNumLandmarks(1);
            }
        }
    }

    /**
     * Transforms a list of tracked objects into landmarks
     * apply transformation on the coordinates of trackedObjects transforming
     * them from local to global using the current pose of the robot.
     * @param trackedObjects The list of tracked objects to transform.
     * @param currentPose    The current pose of the robot.
     * @return A list of global landmarks.
     */
    public List<LandMark> transformToGlobal(List<TrackedObject> trackedObjects, Pose currentPose) {
        List<LandMark> globalLandmarks = new ArrayList<>();

        for (TrackedObject trackedObject : trackedObjects) {
            String id = trackedObject.getId();
            String desc = trackedObject.getDescription();
            List<CloudPoint> globalPoints = new ArrayList<>();
            for (CloudPoint point : trackedObject.getCoordinates()) {

                // transformation: using rotation matrix and adding the x y of the robot's current pose.
                double globalX = point.getX() * Math.cos(currentPose.getYaw()) - point.getY() * Math.sin(currentPose.getYaw()) + currentPose.getX();
                double globalY = point.getX() * Math.sin(currentPose.getYaw()) + point.getY() * Math.cos(currentPose.getYaw()) + currentPose.getY();
                globalPoints.add(new CloudPoint(globalX, globalY));
            }
            globalLandmarks.add(new LandMark(id, desc, globalPoints));
        }

        return globalLandmarks;
    }

}
