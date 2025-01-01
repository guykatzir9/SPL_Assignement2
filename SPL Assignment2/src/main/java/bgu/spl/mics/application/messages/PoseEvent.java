package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;

import java.util.List;

/**
 * this event is being sent by the Pose service and
 * processed by the fusion - SLAM in order to update the robot's position
 * for calculations based on TrackObjectsEvent it has received
 */

public class PoseEvent implements Event<Pose> {
    private final Pose pose;

    public PoseEvent(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose () {
        return this.pose;
    }
}
