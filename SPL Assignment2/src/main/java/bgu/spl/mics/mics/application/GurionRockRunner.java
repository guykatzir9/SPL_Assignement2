package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.Config;
import bgu.spl.mics.application.objects.DataLoader;

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
        System.out.println(Config.getLidarWorkers().get(0).getLiDarDataBase().getCloudPoints("Wall_2", 1).get(0).getX());




        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}
