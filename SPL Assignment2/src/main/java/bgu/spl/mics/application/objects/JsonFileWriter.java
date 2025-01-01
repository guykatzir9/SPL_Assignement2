package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
        }
    }
}
