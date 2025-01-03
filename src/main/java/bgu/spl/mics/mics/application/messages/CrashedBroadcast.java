package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * this broadcast can send by all the sensors to signal
 * the other sensors that the sending sensor crashed
 */

public class CrashedBroadcast implements Broadcast {
    private final String senderName;

    public CrashedBroadcast(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}


