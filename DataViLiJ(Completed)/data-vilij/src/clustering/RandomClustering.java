package clustering;


import algorithm.Clusterer;
import dataprocessors.DataSet;
import javafx.geometry.Point2D;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class RandomClustering extends Clusterer {

    private DataSet       dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private final String        algorithmType;


    public RandomClustering(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters,boolean tocontinue, String algorithmType) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.algorithmType = algorithmType;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }
    
    @Override
    public void run() {
        int iteration = 0;
        while (iteration++ < maxIterations) {
            assignLabels();
            if (iteration % updateInterval == 0) {
                System.out.printf("Iteration number %d \n", iteration); //
                dataset.addData(null, algorithmType,tocontinue());
            }
        }
        dataset.done();
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            dataset.getLabels().put(instanceName, Integer.toString((int)(Math.random()*numberOfClusters)));
        });
    }
    
}

