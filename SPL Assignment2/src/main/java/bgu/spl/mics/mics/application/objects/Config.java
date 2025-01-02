package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the configuration loaded from the JSON file.
 */
public class Config {
    private static String outputFilePath;
    private static String lidarDataBasePath;
    private static List<Camera> cameras;
    private static List<LiDarWorkerTracker> lidarWorkers;
    private static String poseJsonFile;
    private static int tickTime;
    private static int duration;

    // Setters and Getters
    public static void setOutputFilePath(String path) {
        outputFilePath = path;
    }

    public static String getOutputFilePath() {
        return outputFilePath;
    }

    public static void setLidarDataBasePath(String path) {
        lidarDataBasePath = path;
    }

    public static String getLidarDataBasePath() {
        return lidarDataBasePath;
    }

    public static void setCameras(List<Camera> camerasList) {
        cameras = camerasList;
    }

    public static List<Camera> getCameras() {
        return cameras;
    }

    public static void setLidarWorkers(List<LiDarWorkerTracker> workers) {
        lidarWorkers = workers;
    }

    public static List<LiDarWorkerTracker> getLidarWorkers() {
        return lidarWorkers;
    }

    public static void setPoseJsonFile(String path) {
        poseJsonFile = path;
    }

    public static String getPoseJsonFile() {
        return poseJsonFile;
    }

    public static void setTickTime(int time) {
        tickTime = time;
    }

    public static int getTickTime() {
        return tickTime;
    }

    public static void setDuration(int durationTime) {
        duration = durationTime;
    }

    public static int getDuration() {
        return duration;
    }
}