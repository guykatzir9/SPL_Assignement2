package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    private static LiDarDataBase instance = null;
    private final List<StampedCloudPoints> StampedCloudPoints;

    private LiDarDataBase (List<StampedCloudPoints> StampedCloudPoints) {
        this.StampedCloudPoints = StampedCloudPoints;
    }


    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
         if (instance == null) {
             instance = new LiDarDataBase(DataLoader.loadLiDARData(filePath));
         }
        return instance;
    }

    public List<StampedCloudPoints> getStampedCloudPoints() {
        return StampedCloudPoints;
    }


    /**
     * method to extract the list of CloudPoints from a specific StampedCloudPoints
     * @param id represent the id of the StampedCloudPoints
     * @param time represents the time of the StampedCloudPoints
     * @return the List<CloudPoint> CloudPoints of the corresponding StampedCloudPoints
     */
    public List<CloudPoint> getCloudPoints(String id , int time) {

        for (StampedCloudPoints SCP : this.StampedCloudPoints ) {

            if (SCP.getTime() == time && SCP.getId().equals(id) ) {
                return SCP.getCloudPoints();
            }

        }
        return null;
    }

    public Boolean ErrorAtTick(int tick) {
        for (StampedCloudPoints SCP : StampedCloudPoints) {
            if (SCP.getTime() == tick && SCP.getId().equals("ERROR")) {
                return true;
            }
        }
        return false;
    }
}
