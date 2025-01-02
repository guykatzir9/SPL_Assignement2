package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataLoader {

    /**
     * Loads camera data from a JSON file into a structured Map.
     * @param filePath The path to the JSON file containing camera data.
     * @return A Map where each key is a camera ID and the value is a list of StampedDetectedObjects.
     */
    public static Map<String, List<StampedDetectedObjects>> loadCameraData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
            return gson.fromJson(reader, mapType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load camera data from file: " + filePath);
        }
    }

    /**
     * Loads LiDAR data from a JSON file into a structured List of StampedCloudPoints.
     * Processes each raw entry from the JSON to create StampedCloudPoints objects.
     *
     * @param filePath The path to the JSON file containing LiDAR data.
     * @return A List of StampedCloudPoints objects.
     */
    public static List<StampedCloudPoints> loadLiDARData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> rawData = gson.fromJson(reader, listType);

            List<StampedCloudPoints> stampedCloudPointsList = new ArrayList<>();

            for (Map<String, Object> entry : rawData) {
                String id = (String) entry.get("id");
                int time = ((Double) entry.get("time")).intValue(); // JSON numbers are parsed as Double
                List<List<Double>> rawCloudPoints = (List<List<Double>>) entry.get("cloudPoints");

                List<CloudPoint> cloudPoints = convertToCloudPoints(rawCloudPoints);
                stampedCloudPointsList.add(new StampedCloudPoints(id, time, cloudPoints));
            }

            return stampedCloudPointsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load LiDAR data from file: " + filePath);
        }
    }

    /**
     * Loads Pose data from a JSON file into a structured List.
     * @param filePath The path to the JSON file containing Pose data.
     * @return A List of Pose objects.
     */
    public static List<Pose> loadPoseData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Pose data from file: " + filePath);
        }
    }

    /**
     * Loads Configuration data from a JSON file and updates the Config class with its values.
     * @param filePath The path to the JSON configuration file.
     */
    public static void loadConfigurationData(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            RawConfiguration rawConfig = gson.fromJson(reader, RawConfiguration.class);

            // Convert Cameras
            List<Camera> cameras = new ArrayList<>();
            for (RawConfiguration.RawCameraConfiguration config : rawConfig.Cameras.CamerasConfigurations) {
                cameras.add(new Camera(config.id, config.frequency, loadCameraData("example_input_2/camera_data.json").get("camera" + config.id), rawConfig.Duration));
            }

            // Convert LiDar Workers
            List<LiDarWorkerTracker> lidarWorkers = new ArrayList<>();
            for (RawConfiguration.RawLiDarConfiguration config : rawConfig.LiDarWorkers.LidarConfigurations) {
                lidarWorkers.add(new LiDarWorkerTracker(config.id, config.frequency));
            }

            // Update Config class
            Config.setOutputFilePath(filePath);
            Config.setLidarDataBasePath(rawConfig.LiDarWorkers.lidars_data_path);
            Config.setCameras(cameras);
            Config.setLidarWorkers(lidarWorkers);
            Config.setPoseJsonFile(rawConfig.poseJsonFile);
            Config.setTickTime(rawConfig.TickTime);
            Config.setDuration(rawConfig.Duration);

        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration data from file: " + filePath);
        }
    }

    private static class RawConfiguration {
        private RawCameras Cameras;
        private RawLiDarWorkers LiDarWorkers;
        private String poseJsonFile;
        private int TickTime;
        private int Duration;

        private static class RawCameras {
            private List<RawCameraConfiguration> CamerasConfigurations;
        }

        private static class RawLiDarWorkers {
            private List<RawLiDarConfiguration> LidarConfigurations;
            private String lidars_data_path;
        }

        private static class RawCameraConfiguration {
            private int id;
            private int frequency;
            private String camera_key;
        }

        private static class RawLiDarConfiguration {
            private int id;
            private int frequency;
        }
    }

    /**
     * Converts a raw list of points from the LiDAR JSON file to CloudPoint objects.
     * Ignores the Z-coordinate and uses only X and Y.
     * @param rawPoints The raw list of points from the JSON file.
     * @return A List of CloudPoint objects.
     */
    private static List<CloudPoint> convertToCloudPoints(List<List<Double>> rawPoints) {
        List<CloudPoint> cloudPoints = new ArrayList<>();
        for (List<Double> point : rawPoints) {
            if (point.size() >= 2) { // Make sure we have at least x and y
                cloudPoints.add(new CloudPoint(point.get(0), point.get(1))); // Use only x and y
            }
        }
        return cloudPoints;
    }
}










