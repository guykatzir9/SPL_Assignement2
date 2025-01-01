package bgu.spl.mics.application.objects;

import java.util.List;

public class Output {

    private final List<LandMark> landMarkList = FusionSlam.getInstance().getLandMarks();
    private final StatisticalFolder statistics = StatisticalFolder.getInstance();

    public StatisticalFolder getStatistics() {
        return statistics;
    }

    public List<LandMark> getLandMarkList() {
        return landMarkList;
    }




}


