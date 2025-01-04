package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonFileWriter {
    /**
     * Converts an object to JSON format and writes it to the specified file path.
     *
     * @param object   The object to be converted to JSON.
     * @param filePath The full path (including the file name) where the JSON will be saved.
     * @throws IOException If an I/O error occurs during writing.
     */
    public static void writeObjectToJsonFile(Object object, String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter("./"+filePath)) {
            gson.toJson(object, writer);
        }
    }
    public static void writeObjectToJsonFileInSameDirectory(Object object, String referenceFilePath, String jsonFileName) throws IOException {
        // Validate the reference file path
        File referenceFile = new File(referenceFilePath);
        if (!referenceFile.exists()) {
            throw new IOException("Reference file does not exist: " + referenceFilePath);
        }

        // Determine the directory of the reference file
        File directory = referenceFile.getParentFile();
        if (directory == null) {
            throw new IOException("Could not determine the directory of the reference file.");
        }

        // Create the new JSON file in the same directory
        File jsonFile = new File(directory, jsonFileName);

        // Create a Gson instance
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Write the object as JSON to the new file
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(object, writer);
        }

        System.out.println("JSON file created successfully at " + jsonFile.getAbsolutePath());
    }
}


