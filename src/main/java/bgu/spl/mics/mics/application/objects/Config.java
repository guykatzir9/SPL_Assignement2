package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * representing the total data extracting from the configurationFile.
 */
public class Config {
    private static String outputFilePath;
    private static String lidarDataBasePath;
    private static String camerasDataPath;
    private static List<Camera> cameras;
    private static List<LiDarWorkerTracker> lidarWorkers;
    private static String poseJsonFile;
    private static int tickTime;
    private static int duration;
    private static int totalEvents;

    public static String getOutputFilePath() {
        return outputFilePath;
    }

    public static void setOutputFilePath(String outputFilePath) {
        Config.outputFilePath = outputFilePath;
    }

    public static String getLidarDataBasePath() {
        return lidarDataBasePath;
    }

    public static void setLidarDataBasePath(String lidarDataBasePath) {
        Config.lidarDataBasePath = lidarDataBasePath;
    }

    public static String getCamerasDataPath() {
        return camerasDataPath;
    }

    public static void setCamerasDataPath(String camerasDataPath) {
        Config.camerasDataPath = camerasDataPath;
    }

    public static List<Camera> getCameras() {
        return cameras;
    }

    public static void setCameras(List<Camera> cameras) {
        Config.cameras = cameras;
    }

    public static List<LiDarWorkerTracker> getLidarWorkers() {
        return lidarWorkers;
    }

    public static void setLidarWorkers(List<LiDarWorkerTracker> lidarWorkers) {
        Config.lidarWorkers = lidarWorkers;
    }

    public static String getPoseJsonFile() {
        return poseJsonFile;
    }

    public static void setPoseJsonFile(String poseJsonFile) {
        Config.poseJsonFile = poseJsonFile;
    }

    public static int getTickTime() {
        return tickTime;
    }

    public static void setTickTime(int tickTime) {
        Config.tickTime = tickTime;
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        Config.duration = duration;
    }

    public static int getTotalEvents() {
        return totalEvents;
    }

    public static void setTotalEvents(int totalEvents) {
        Config.totalEvents = totalEvents;
    }
}
