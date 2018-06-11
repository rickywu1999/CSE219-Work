/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ricky1999
 */
package classification;

import dataprocessors.DataSet;
import algorithm.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private final String algorithmType;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }
    
    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue,
                            String algorithmType) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.algorithmType = algorithmType;
    }

    @Override
    public void run() {
        int i = 1;
        while (i <= maxIterations ){
            int xCoefficient = 0;
            while (xCoefficient == 0){
                xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            }
            
            int yCoefficient = 0;
            while (yCoefficient == 0){
                yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            }
            
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
           
            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
                dataset.addData(output, algorithmType,tocontinue());
            }
            else if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                dataset.addData(output, algorithmType,tocontinue());
                dataset.done();
                break;
            }
            i++;
        }
        dataset.done();
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
    }
}
