package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

import java.util.List;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera Mycamera;

    /**
     *
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("camera service" + camera.getId());
        this.Mycamera = camera;

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
            int relevantDetectionTick = currTick - Mycamera.getFrequency();
            if (Mycamera.isUP()) {
                // create an event with the StampedDetectionObjects matches to the relevant Tick and send it
                StampedDetectedObjects TickObjects = Mycamera.getObjectAtTick(relevantDetectionTick);
                if (TickObjects != null) {

                    // updating the camera last frames before checking them if there is error
                    LastFrames.getInstance().setCameras( "camera" + Mycamera.getId() , TickObjects );
                    // checking for error
                    for (DetectedObject DO : TickObjects.getDetectedObjects()) {
                        if (DO.getId().equals("ERROR")) {
                            // create an OutputError file, send CrashedBroadcast,set status to error and terminate
                            OutputError error = new OutputError(DO.getDescription(), "camera" + Mycamera.getId());
                            JsonFileWriter.writeObjectToJsonFile( error , Config.getOutputFilePath());
                            sendBroadcast(new CrashedBroadcast(this.getName()));
                            Mycamera.setStatus(STATUS.ERROR);
                            this.terminate();
                        }
                        // if it is not an error object we will increment the total detected objects.
                        StatisticalFolder.getInstance().incrementNumDetectedObjects(1);
                    }
                    // if TickObjects includes no error we will send it as an event
                    DetectObjectsEvent TickObjectsEvent = new DetectObjectsEvent(TickObjects , relevantDetectionTick, currTick);
                    sendEvent(TickObjectsEvent);
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
    }
}
