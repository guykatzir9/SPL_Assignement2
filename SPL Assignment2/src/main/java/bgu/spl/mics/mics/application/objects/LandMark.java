package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {

        private final String id;
        private final String description;
        private List<CloudPoint> coordinates;


        public LandMark(String id, String description, List<CloudPoint> coordinates) {
            this.id = id;
            this.description = description;
            this.coordinates = coordinates;
        }

        // Getter for ID
        public String getId() {
            return id;
        }

        // Getter for Description
        public String getDescription() {
            return description;
        }

        public List<CloudPoint> getCoordinates() {
            return coordinates;
        }

    public void refineLocation(List<CloudPoint> newCloudPoints) {
        List<CloudPoint> refinedCloudPoints = new ArrayList<>();
        int minSize = Math.min(coordinates.size(), newCloudPoints.size());

        for (int i = 0; i < minSize; i++) {
            CloudPoint oldPoint = coordinates.get(i);
            CloudPoint newPoint = newCloudPoints.get(i);

            double avgX = (oldPoint.getX() + newPoint.getX()) / 2;
            double avgY = (oldPoint.getY() + newPoint.getY()) / 2;

            refinedCloudPoints.add(new CloudPoint(avgX, avgY));
        }

        if (coordinates.size() > minSize) {
            refinedCloudPoints.addAll(coordinates.subList(minSize, coordinates.size()));
        }

        else if (newCloudPoints.size() > minSize) {
            refinedCloudPoints.addAll(newCloudPoints.subList(minSize, newCloudPoints.size()));
        }

        this.coordinates = refinedCloudPoints;
    }
}







