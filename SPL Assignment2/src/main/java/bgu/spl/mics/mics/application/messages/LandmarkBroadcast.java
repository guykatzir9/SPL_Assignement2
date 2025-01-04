package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.LandMark;

import java.util.List;

public class LandmarkBroadcast implements Broadcast {
    public LandmarkBroadcast(List<LandMark> landMarkList){
        System.out.println("Landmarks updated: " + landMarkList);
    }

}
