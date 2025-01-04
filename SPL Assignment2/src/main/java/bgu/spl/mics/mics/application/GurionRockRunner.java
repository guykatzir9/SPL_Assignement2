package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        String configurationPath = args[0];
        DataLoader.loadConfigurationData(configurationPath);

        System.out.println(Config.getLidarDataBasePath());
        System.out.println(Config.getCamerasDataPath());
        System.out.println(Config.getTotalEvents());
        System.out.println(Config.getCameras().get(0).getTerminationTime());
        System.out.println(Config.getPoseJsonFile());
        System.out.println(Config.getDuration());











        // Determine the number of services to initialize
        int serviceCount = Config.getCameras().size() + Config.getLidarWorkers().size() + 3; // SensorManagerService, PoseService, FusionSlamService
        CountDownLatch latch = new CountDownLatch(serviceCount);

        int sensorCount = Config.getCameras().size() + Config.getLidarWorkers().size() + 1; // Cameras + Lidars + FusionSlamService
        CountDownLatch sensorLatch = new CountDownLatch(sensorCount);

        //Initialize sensor manager
        SensorManagerService sms = new SensorManagerService(Config.getTotalEvents(),latch);
        Thread sensorManagerThread = new Thread(sms);
        sensorManagerThread.start();

        //Initialize cameras
        for(Camera camera : Config.getCameras()){
            CameraService cameraService = new CameraService(camera, latch);
            Thread cameraThread = new Thread(cameraService);
            cameraThread.start();
            sms.AddSensor(cameraService);
            sensorLatch.countDown();
        }

        //Initialize lidars
        for(LiDarWorkerTracker liDarWorkerTracker : Config.getLidarWorkers()){
            LiDarService liDarService = new LiDarService(liDarWorkerTracker, latch);
            Thread lidarThread = new Thread(liDarService);
            lidarThread.start();
            sms.AddSensor(liDarService);
            sensorLatch.countDown();
        }

        //Initialize Pose
        Thread poseThread = new Thread(new PoseService(new GPSIMU(), latch));
        poseThread.start();

        //Initialize fusion-slam
        FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance(), latch);
        Thread Fusionthread = new Thread(fusionSlamService);
        Fusionthread.start();
        sms.AddSensor(fusionSlamService);
        sensorLatch.countDown();


        // Wait for all services to finish initializing
        try {
            latch.await(); // Main thread blocks here until the latch count reaches zero
            System.out.println("All services initialized. Starting TimeService.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Initialization was interrupted.");
        }

        Thread timeThread = new Thread(new TimeService(Config.getTickTime(), Config.getDuration()));
        timeThread.start();
    }

}

