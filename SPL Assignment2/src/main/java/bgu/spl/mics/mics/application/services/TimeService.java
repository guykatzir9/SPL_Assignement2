package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.stopTimeBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final int TickTime;
    private int CurrentTick;
    private final int Duration;
    private Thread timeThread;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
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

        subscribeBroadcast(stopTimeBroadcast.class, b -> {
            if (timeThread != null && timeThread.isAlive()) {
                timeThread.interrupt();
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, b ->{
            if (timeThread != null && timeThread.isAlive()) {
                timeThread.interrupt();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, b -> {
            if (timeThread != null && timeThread.isAlive()) {
                timeThread.interrupt();
            }
        });

        timeThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && CurrentTick < Duration) {
                    CurrentTick++;
                    StatisticalFolder.getInstance().incrementSystemRuntine(1);

                    Broadcast TickBroadcast = new TickBroadcast(CurrentTick);
                    sendBroadcast(TickBroadcast);

                    Thread.sleep(TickTime * 1000);
                }

                sendBroadcast(new TerminatedBroadcast());
                System.out.println("Time is over");

            } catch (InterruptedException e) {
                System.out.println("TimeService interrupted.");
                Thread.currentThread().interrupt();
            }
        });

        timeThread.start();
    }
}
