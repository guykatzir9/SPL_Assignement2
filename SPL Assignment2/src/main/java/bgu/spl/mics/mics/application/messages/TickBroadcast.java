package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * this broadcast is sent by the time service to all the
 * sensors and inform them about the current tick
 * for timing messages publications and processes.
 */

public class TickBroadcast implements Broadcast {
    private final int tick;

    public TickBroadcast(int tick) {
        this.tick = tick;
        System.out.println("Tick: " + this.tick);
    }

    public int getTick () {
        return this.tick;
    }
}
