package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusionSlam;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam service");
        this.fusionSlam = FusionSlam.getInstance();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        // subscribe to TickBroadcast. callback: nothing
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {});

        // subscribe to PoseEvent. callback: add the event pose to the fusion slam poses list.
        subscribeEvent(PoseEvent.class, poseEvent -> {
            fusionSlam.updatePose(poseEvent.getPose());
            complete(poseEvent,true);
        });


        subscribeEvent(TrackedObjectsEvent.class, TrackedEvent ->{
            List<LandMark> globalLandmarks = fusionSlam.transformToGlobal(TrackedEvent.getTrackedObjects(), fusionSlam.getPoses().get(fusionSlam.getPoses().size() - 1));
            fusionSlam.updateLandmarks(globalLandmarks);
            // notify the service manager
            sendBroadcast(new LandmarkBroadcast());
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
            JsonFileWriter.writeObjectToJsonFile(new Output(),Config.getOutputFilePath());
            this.terminate();
        });

        subscribeBroadcast(OutputBroadcast.class, b->{
            fusionSlam.setStatus(STATUS.DOWN);
            JsonFileWriter.writeObjectToJsonFile(new Output(),Config.getOutputFilePath());
            terminate();
        });
    }
}
