package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.STATUS;

import java.util.HashMap;
import java.util.Map;

public class SensorManagerService extends MicroService {

    private Map<MicroService , STATUS> SensorStatus;
    private int UpSensors;
    private int RemainingDetectedEvents;
    private int RemainingTrackedEvents;
    private int RemainingFusionEvents;

    // gets as an input the number of remaining detectedObjectsEvents to send
    // extracting it from the camera data json at the beginning of the program execution
    //in our logic each DetectedObjectsEvent maps to 1 TrackedObjectsEvent maps to 1 "FusionEvent"

    public SensorManagerService (int totalEvents) {
        super("SensorManagerService");
        this.RemainingDetectedEvents = totalEvents;
        this.RemainingTrackedEvents = totalEvents;
        this.RemainingFusionEvents = totalEvents;
        this.SensorStatus = new HashMap<>();
        this.UpSensors = 0;
    }

    public void AddSensor (MicroService microService) {

        SensorStatus.put(microService , STATUS.UP);
        UpSensors ++;
    }
    /**
     * Initializes the ManagerService.
     * Registers the service to handle
     *
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(DetectedObjectsBroadcast.class , b ->{
            RemainingDetectedEvents --;
        });

        subscribeBroadcast(SensorTerminationBroadcast.class , b ->{
            // notify the manager service that this sensor status is down now.
            this.SensorStatus.replace(b.getSenderSensor(), STATUS.DOWN);
            UpSensors --;

            // unused Broadcast right now
            if (RemainingDetectedEvents == 0 && UpSensors == 0) {
                // sending broadcast to the fusionSlam notify it that all sensors have terminated
                sendBroadcast(new AllSensorsTermainatedBroadcast());
            }
        });

        subscribeBroadcast(TrackedObjectsBroadcast.class, b -> {
            RemainingTrackedEvents --;
            if (RemainingTrackedEvents == 0) {
                // set the all the LiDars down
                sendBroadcast(new LiDarTerminationBroadcast());
            }

        });

        subscribeBroadcast(LandmarkBroadcast.class, b->{
            RemainingFusionEvents --;
            if (RemainingFusionEvents == 0) {
                sendBroadcast(new OutputBroadcast());
                terminate();
            }
        });

    }
}
