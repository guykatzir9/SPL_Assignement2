package bgu.spl.mics.application.objects;

public class Config {
    private static String outputFilePath;

    public static void setOutputFilePath(String path) {
        outputFilePath = path;
    }

    public static String getOutputFilePath() {
        return outputFilePath;
    }
}


