package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    private final GPSIMU gpsimu;
    private static final List<Pose> poses = new ArrayList<>();

    public static List<Pose> getPoses() {
        return poses;
    }

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("The PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService. // assuming there exists one.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {

        // subscribe to TickBroadcast. callback: each tick send a PoseEvent
        // with the current pose.
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            gpsimu.setCurrentTick(tickBroadcast.getTick());
            Pose currentPose = gpsimu.getCurrentPose();
            poses.add(currentPose);
            sendEvent(new PoseEvent(currentPose));

        } );

        // subscribe to CrashedBroadcast. callback: set the status to error
        // (unplanned termination) and terminate the MicroService
        subscribeBroadcast(CrashedBroadcast.class, terminated -> {
            gpsimu.setStatus(STATUS.ERROR);
            this.terminate();
        });
    }
}
