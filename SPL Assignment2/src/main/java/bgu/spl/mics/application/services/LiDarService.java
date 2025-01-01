package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker MyLiDar;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService " + LiDarWorkerTracker.getId());
        this.MyLiDar = LiDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {

        // subscribe to TickBroadcast. callback: when MyLiDar can send an event
        // it sends TrackedObjectEvent created from his lastTrackedObjects field

        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            int currTick = tickBroadcast.getTick();
            for (Integer LiDarIsReady : MyLiDar.getTrackObjectsMap().keySet()) {
                int processedTime = MyLiDar.getTrackObjectsMap().get(LiDarIsReady).get(0).getProcessedTime();
                // conditions which TrackedObjects processed by the LiDar can now be sent as events.
                if (LiDarIsReady < processedTime || LiDarIsReady == currTick) {

                    // extracting the TrackObjects list at key LiDarIsReady
                    List<TrackedObject> TrackedObjectsToSend = MyLiDar.getTrackObjectsMap().get(LiDarIsReady);
                    //updating my lidar lastTrackedObjects field
                    MyLiDar.setLastTrackedObjects(TrackedObjectsToSend);
                    // updating last frames before checking for an error.
                    LastFrames.getInstance().setLiDars("LiDar" + MyLiDar.getId(), MyLiDar.getLastTrackedObjects());
                    // checking for an error
                    for (TrackedObject TO : TrackedObjectsToSend) {
                        if (TO.getId().equals("ERROR")){
                            // create an OutputError file, send CrashedBroadcast,set status to error and terminate
                            OutputError error = new OutputError(TO.getDescription(),"LiDar" + MyLiDar.getId());
                            JsonFileWriter.writeObjectToJsonFile( error , Config.getOutputFilePath());
                            sendBroadcast(new CrashedBroadcast(this.getName()));
                            MyLiDar.setStatus(STATUS.ERROR);
                            this.terminate();
                        }
                        // if it is not an error object we will increment the total Tracked objects.
                        StatisticalFolder.getInstance().incrementNumTrackedObjects(1);
                    }
                    // if TrackedObjectsToSend includes no errors we will send it as an event.

                    // extracting detection time of all those TrackedObjects. this time will
                    // be equal to all of them because detectionTime + lidar freq = LiDarIsReady
                    int detectionTime = LiDarIsReady - MyLiDar.getFrequency();
                    TrackedObjectsEvent TickTrackedObjectsEvent = new TrackedObjectsEvent(TrackedObjectsToSend, detectionTime);
                    sendEvent(TickTrackedObjectsEvent);
                    // remove those TrackObjects from the map of TrackObjects need to be sent.
                    MyLiDar.getTrackObjectsMap().remove(LiDarIsReady);
                }

            }
        }   );

        // subscribe to TerminatedBroadcast. callback: set the status down
        // (planned termination) and terminate the MicroService
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            MyLiDar.setStatus(STATUS.DOWN);
            this.terminate();
        });


        // subscribe to CrashedBroadcast. callback: set the status to error
        // (unplanned termination) and terminate the MicroService
        subscribeBroadcast(CrashedBroadcast.class, terminated -> {
            MyLiDar.setStatus(STATUS.ERROR);
            this.terminate();
        });

        // subscribe to DetectedObjectsEvent. callback: process the data from
        // the detected objects event and update MYLiDar lastTrackedObjects.
        subscribeEvent(DetectObjectsEvent.class, event -> {
            List<TrackedObject> lastTrackedObjects = MyLiDar.processDetectedObjectsEvent(event);
            MyLiDar.setLastTrackedObjects(lastTrackedObjects);
            int LiDarIsReady = event.getDetectionTick() + MyLiDar.getFrequency();
            MyLiDar.addToMap(LiDarIsReady , lastTrackedObjects);
            MyLiDar.setLastDetectionTick(event.getDetectionTick());
            MyLiDar.setLastSentTick(event.getSendingTick());
        } );

    }
}
