package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusionSlam;
    private final CountDownLatch latch;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     * @param latch
     */
    public FusionSlamService(FusionSlam fusionSlam, CountDownLatch latch) {
        super("FusionSlam service");
        this.fusionSlam = fusionSlam.getInstance();
        this.latch = latch;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        // subscribe to PoseEvent. callback: add the event pose to the fusion slam poses list.
        subscribeEvent(PoseEvent.class, poseEvent -> {
            fusionSlam.updatePose(poseEvent.getPose());
            complete(poseEvent,true);
        });


        subscribeEvent(TrackedObjectsEvent.class, TrackedEvent ->{

            List<LandMark> globalLandmarks = fusionSlam.transformToGlobal(TrackedEvent.getTrackedObjects(), fusionSlam.getPoseAtTick(TrackedEvent.getTick()));
            fusionSlam.updateLandmarks(globalLandmarks);
            // notify the service manager
            sendBroadcast(new LandmarkBroadcast(globalLandmarks));
            complete(TrackedEvent,true);
        });

        // subscribe to CrashedBroadcast. callback: set the status to error
        // (unplanned termination) and terminate the MicroService
        subscribeBroadcast(CrashedBroadcast.class, terminated -> {
            fusionSlam.setStatus(STATUS.ERROR);
            this.terminate();
        });

        // subscribe to TerminatedBroadcast. callback: set the status down
        // (planned termination) and terminate the MicroService
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {

            fusionSlam.setStatus(STATUS.DOWN);
            JsonFileWriter.writeObjectToJsonFileInSameDirectory(new Output(), Config.getConfigurationPath(), "T_output.json");
            this.terminate();
        });

        subscribeBroadcast(OutputBroadcast.class, b->{

            fusionSlam.setStatus(STATUS.DOWN);
            JsonFileWriter.writeObjectToJsonFileInSameDirectory(new Output(), Config.getConfigurationPath(), "T_output.json");
            sendBroadcast(new stopTimeBroadcast());
            sendBroadcast(new TerminatedBroadcast());
            terminate();

        });
        latch.countDown();
        System.out.println(this.getName() + " initialized ");
    }
}
