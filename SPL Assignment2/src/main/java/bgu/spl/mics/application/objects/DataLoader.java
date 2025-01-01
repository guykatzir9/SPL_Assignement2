package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
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
     * Loads LiDAR data from a JSON file into a structured List.
     * @param filePath The path to the JSON file containing LiDAR data.
     * @return A List of StampedCloudPoints objects.
     */
    public static List<StampedCloudPoints> loadLiDARData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            return gson.fromJson(reader, listType);
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
}








