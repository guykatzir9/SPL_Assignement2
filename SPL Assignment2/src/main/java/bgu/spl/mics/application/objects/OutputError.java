package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.PoseService;

import java.util.ArrayList;
import java.util.List;

public class OutputError {

    private final String SourceDescription;
    private final String faultySensor;
    private final LastFrames lastFrames = LastFrames.getInstance();
    private final List<Pose> poses = PoseService.getPoses();
    private final StatisticalFolder statistics = StatisticalFolder.getInstance();
    private final List<LandMark> landMarks = FusionSlam.getInstance().getLandMarks();

    public OutputError(String sourceDescription, String faultySensor) {
        this.SourceDescription = sourceDescription;
        this.faultySensor = faultySensor;
    }


    public List<Pose> getPoses() {
        return poses;
    }

    public LastFrames getLastFrames() {
        return lastFrames;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public String getSourceDescription() {
        return SourceDescription;
    }

    public StatisticalFolder getStatistics() {
        return statistics;
    }


}
