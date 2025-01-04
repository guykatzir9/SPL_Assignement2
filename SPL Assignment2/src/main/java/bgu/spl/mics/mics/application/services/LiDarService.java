package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

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
    private final CountDownLatch latch;
    private int currTick;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     * @param latch
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, CountDownLatch latch) {
        super("LiDarService " + LiDarWorkerTracker.getId());
        this.MyLiDar = LiDarWorkerTracker;
        this.latch = latch;
        this.currTick = 0;
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

            currTick = tickBroadcast.getTick();
            // checking for an error at this tick
            if (MyLiDar.getLiDarDataBase().ErrorAtTick(currTick)) {
                // create an OutputError file, send CrashedBroadcast,set status to error and terminate
                OutputError error = new OutputError("connection to LiDar lost", "LiDar" + MyLiDar.getId());
                JsonFileWriter.writeObjectToJsonFileInSameDirectory(error, Config.getConfigurationPath(), "Error_output.json");
                sendBroadcast(new CrashedBroadcast(this.getName()));
                MyLiDar.setStatus(STATUS.ERROR);
                this.terminate();
            }

            // continue if there was no error
            if (MyLiDar.isUp()) {
                List<List<TrackedObject>> currentList = MyLiDar.getTrackObjectsMap().remove(currTick);
                if (currentList != null) {
                        for (List<TrackedObject> tempList : currentList) {
                            //updating my lidar lastTrackedObjects field
                            MyLiDar.setLastTrackedObjects(tempList);
                            // updating last frames for this LiDar
                            LastFrames.getInstance().setLiDars("LiDar" + MyLiDar.getId(), MyLiDar.getLastTrackedObjects());
                            // increment the total Tracked objects before sending the event
                            StatisticalFolder.getInstance().incrementNumTrackedObjects(tempList.size());

                            System.out.println("numTrackedObjectsEvent increased in " + tempList.size());
                            // extracting detection time of all those TrackedObjects. this time will
                            // be equal to all of them because detectionTime + lidar freq = LiDarIsReady = currTick here
                            int detectionTime = currTick - MyLiDar.getFrequency();
                            TrackedObjectsEvent tempTrackedObjectsEvent = new TrackedObjectsEvent(tempList, detectionTime, "LiDar" + MyLiDar.getId());
                            sendEvent(tempTrackedObjectsEvent);

                            // notify the service manager
                            sendBroadcast(new TrackedObjectsBroadcast());
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

            System.out.println("lidar" + MyLiDar.getId() + " got detcted objects for processing " +  event.getStampedDetectedObjects());
            List<TrackedObject> processedDetectedObjects = MyLiDar.processDetectedObjectsEvent(event);
            int LiDarIsReady = event.getDetectionTick() + MyLiDar.getFrequency();

            // check if the event can be sent immediately.
            if (LiDarIsReady <= currTick) {

                // updating statistics,last frames for this LiDar and send the event
                MyLiDar.setLastTrackedObjects(processedDetectedObjects);
                LastFrames.getInstance().setLiDars("LiDar" + MyLiDar.getId(), MyLiDar.getLastTrackedObjects());
                StatisticalFolder.getInstance().incrementNumTrackedObjects(processedDetectedObjects.size());
                TrackedObjectsEvent tempTrackedObjectsEvent = new TrackedObjectsEvent(processedDetectedObjects, event.getDetectionTick(), "LiDar" + MyLiDar.getId());
                sendEvent(tempTrackedObjectsEvent);
            }
            //else add it to the map
            MyLiDar.addToMap(LiDarIsReady , processedDetectedObjects);
            MyLiDar.setLastDetectionTick(event.getDetectionTick());
            MyLiDar.setLastSentTick(event.getSendingTick());

            System.out.println("LiDar" + MyLiDar.getId() + " processed Devent " + event.getDetectionTick() );
        } );

        subscribeBroadcast(LiDarTerminationBroadcast.class, b -> {
            MyLiDar.setStatus(STATUS.DOWN);
            // notify it to the service manager
            sendBroadcast(new SensorTerminationBroadcast(this));
            terminate();
        });
        latch.countDown();
    }
}
