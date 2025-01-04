package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.concurrent.CountDownLatch;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera Mycamera;
    private final CountDownLatch latch;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     * @param latch
     */
    public CameraService(Camera camera, CountDownLatch latch) {
        super("camera service" + camera.getId());
        this.Mycamera = camera;
        this.latch = latch;

    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {

        // subscribe to TickBroadcast. callback: when the current tick matches the camera frequency
        // send DetectObjectsEvent with the list of the detected objects of current tick

        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            int currTick = tickBroadcast.getTick();
            // checking fo an error at this current tick
            StampedDetectedObjects currentTickObjects = Mycamera.getObjectAtTick(currTick);
            if(currentTickObjects != null ){
                System.out.println("camera" + Mycamera.getId() + " Detected objects in current tick: " +  currentTickObjects);
                // checking for error
                for (DetectedObject DO : currentTickObjects.getDetectedObjects()) {
                    if (DO.getId().equals("ERROR")) {
                        // create an OutputError file, send CrashedBroadcast,set status to error and terminate
                        OutputError error = new OutputError(DO.getDescription(), "camera" + Mycamera.getId());
                        JsonFileWriter.writeObjectToJsonFileInSameDirectory(error, Config.getConfigurationPath(), "Error_output.json");
                        sendBroadcast(new CrashedBroadcast(this.getName()));
                        Mycamera.setStatus(STATUS.ERROR);
                        this.terminate();
                    }
                }
            }
            if (Mycamera.isUP()) {
                int relevantDetectionTick = currTick - Mycamera.getFrequency();
                // create an event with the StampedDetectionObjects matches to the relevant Tick and send it
                StampedDetectedObjects TickObjects = Mycamera.getObjectAtTick(relevantDetectionTick);
                if (TickObjects != null) {
                    // updating the camera last frames and the statistics before sending the event
                    LastFrames.getInstance().setCameras( "camera" + Mycamera.getId() , TickObjects );
                    StatisticalFolder.getInstance().incrementNumDetectedObjects(TickObjects.getDetectedObjects().size());
                    DetectObjectsEvent TickObjectsEvent = new DetectObjectsEvent(TickObjects , relevantDetectionTick, currTick, "camera" + Mycamera.getId());
                    System.out.println("camera" + Mycamera.getId() + "Send a DetectedObjects Event" + TickObjectsEvent);
                    sendEvent(TickObjectsEvent);
                    // notify the service manager to decrease the number of detectedObjectsEvent remain to send
                    sendBroadcast(new DetectedObjectsBroadcast());
                }
                if (currTick == Mycamera.getTerminationTime()){
                    sendBroadcast(new SensorTerminationBroadcast(this));
                    terminate();
                }
            }
        } );

        // subscribe to TerminatedBroadcast. callback: set the status down
        // (planned termination) and terminate the MicroService
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            Mycamera.setStatus(STATUS.DOWN);
            this.terminate();
        });


        // subscribe to CrashedBroadcast. callback: set the status to error
        // (unplanned termination) and terminate the MicroService
        subscribeBroadcast(CrashedBroadcast.class, terminated -> {
            Mycamera.setStatus(STATUS.ERROR);
            this.terminate();
        });
        latch.countDown();
    }
}
