package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final int TickTime;
    private int CurrentTick;
    private final int Duration;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, int duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = duration;
        this.CurrentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     * in this specific microservice the initialize method does the microservice task itself
     */
    @Override
    protected void initialize() {
        // we want a different thread to run this method for not blocking the event loop
        //of this microservice when we sleep for tick time.

        Thread TickThread = new Thread(() -> {

            try {
                while (Duration > CurrentTick) {
                    CurrentTick ++;
                    StatisticalFolder.getInstance().incrementSystemRuntine(1);
                    Broadcast TickBroadcast = new TickBroadcast(CurrentTick);
                    sendBroadcast(TickBroadcast);
                    Thread.sleep(TickTime);
                }
                // making all other sensors to stop when tick = duration
                sendBroadcast(new TerminatedBroadcast());
                terminate();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            }
        } );
        TickThread.start();

    }
}
