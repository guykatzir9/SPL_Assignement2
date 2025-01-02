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
            // checking for an error at this tick
            if (MyLiDar.getLiDarDataBase().ErrorAtTick(currTick)) {
                // create an OutputError file, send CrashedBroadcast,set status to error and terminate
                OutputError error = new OutputError("connection to LiDar lost", "LiDar" + MyLiDar.getId());
                JsonFileWriter.writeObjectToJsonFile(error, Config.getOutputFilePath());
                sendBroadcast(new CrashedBroadcast(this.getName()));
                MyLiDar.setStatus(STATUS.ERROR);
                this.terminate();
            }
            if (MyLiDar.isUp()) {
                for (Integer LiDarIsReady : MyLiDar.getTrackObjectsMap().keySet()) {
                    // extracting the list of lists in the key lidar is ready
                    List<List<TrackedObject>> currentList = MyLiDar.getTrackObjectsMap().get(LiDarIsReady);
                    for (List<TrackedObject> tempList : currentList ) {
                        //extracting the processed time from the first trackedObject in this list
                        int processedTime = tempList.get(0).getProcessedTime();
                        // conditions if temp list can be sent as an event now
                        if (LiDarIsReady < processedTime || LiDarIsReady == currTick) {
                            //updating my lidar lastTrackedObjects field
                            MyLiDar.setLastTrackedObjects(tempList);
                            // updating last frames for this LiDar
                            LastFrames.getInstance().setLiDars("LiDar" + MyLiDar.getId(), MyLiDar.getLastTrackedObjects());
                            // increment the total Tracked objects before sending the event
                            StatisticalFolder.getInstance().incrementNumTrackedObjects(tempList.size());
                            // extracting detection time of all those TrackedObjects. this time will
                            // be equal to all of them because detectionTime + lidar freq = LiDarIsReady
                            int detectionTime = LiDarIsReady - MyLiDar.getFrequency();
                            TrackedObjectsEvent tempTrackedObjectsEvent = new TrackedObjectsEvent(tempList, detectionTime);
                            sendEvent(tempTrackedObjectsEvent);
                            // notify the service manager
                            sendBroadcast(new TrackedObjectsBroadcast());
                            currentList.remove(tempList);
                            if (currentList.isEmpty()) {
                                // remove those TrackObjects from the map of TrackObjects need to be sent.
                                MyLiDar.getTrackObjectsMap().remove(LiDarIsReady);
                            }
                        }
                    }

                }

            }
        });

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
            int LiDarIsReady = event.getDetectionTick() + MyLiDar.getFrequency();
            MyLiDar.addToMap(LiDarIsReady , lastTrackedObjects);
            MyLiDar.setLastDetectionTick(event.getDetectionTick());
            MyLiDar.setLastSentTick(event.getSendingTick());
        } );

        subscribeBroadcast(LiDarTerminationBroadcast.class, b -> {
            MyLiDar.setStatus(STATUS.DOWN);
            // notify it to the service manager
            sendBroadcast(new SensorTerminationBroadcast(this));
            terminate();
        });
    }
}
