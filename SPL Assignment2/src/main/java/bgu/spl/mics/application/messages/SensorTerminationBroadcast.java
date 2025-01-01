package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class SensorTerminationBroadcast implements Broadcast {
    private final MicroService SenderSensor;


    public SensorTerminationBroadcast(MicroService senderSensor) {
        SenderSensor = senderSensor;
    }

    public MicroService getSenderSensor() {
        return SenderSensor;
    }
}
