/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.geometry.Point2D;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ricky1999
 */
public class TSDLineTest {
    
    public TSDLineTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of processData method, of class TSDProcessor.
     */
    
    /*
        The method processString takes in a line which is split into 3 parts are put into a HashMap. Boundary cases for each of the 3 cases must be tested:
        *When the label name is one character string
        *When the instance name is one character string
        *When the x and y coordinates are each one digit long
        Additionally, a null string should return a NullPointerError
    */
    
    //Base case where instance name, label name, and data points are all valid
    @Test
    public void testProcessString() throws Exception{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        String tsdString = "@test\ttest\t10,10";
        Map<String,String> dataLabelsExpected = new HashMap<>();
        Map<String,Point2D> dataPointsExpected = new HashMap<>();
        dataLabelsExpected.put("@test", "test");
        dataPointsExpected.put("@test", new Point2D(Double.parseDouble("10"), Double.parseDouble("10")));
        
        Map<String,String> dataLabels = new HashMap<>();
        Map<String,Point2D> dataPoints = new HashMap<>();
        
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
        assertEquals(dataLabels,dataLabelsExpected);
        assertEquals(dataPoints,dataPointsExpected);
    }
    
    //Case when label is a one character string
        @Test
    public void testProcessString2() throws Exception{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        String tsdString = "@test\t1\t10,10";
        Map<String,String> dataLabelsExpected = new HashMap<>();
        Map<String,Point2D> dataPointsExpected = new HashMap<>();
        dataLabelsExpected.put("@test", "1");
        dataPointsExpected.put("@test", new Point2D(Double.parseDouble("10"), Double.parseDouble("10")));
        
        Map<String,String> dataLabels = new HashMap<>();
        Map<String,Point2D> dataPoints = new HashMap<>();
        
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
        assertEquals(dataLabels,dataLabelsExpected);
        assertEquals(dataPoints,dataPointsExpected);
    }
    
    //Case when instance name is a one character string
    @Test
    public void testProcessString3() throws Exception{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        String tsdString = "@1\ttest\t10,10";
        Map<String,String> dataLabelsExpected = new HashMap<>();
        Map<String,Point2D> dataPointsExpected = new HashMap<>();
        dataLabelsExpected.put("@1", "test");
        dataPointsExpected.put("@1", new Point2D(Double.parseDouble("10"), Double.parseDouble("10")));
        
        Map<String,String> dataLabels = new HashMap<>();
        Map<String,Point2D> dataPoints = new HashMap<>();
        
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
        assertEquals(dataLabels,dataLabelsExpected);
        assertEquals(dataPoints,dataPointsExpected);
    }
    
    //Case with null string
    @Test(expected = NullPointerException.class)
    public void testProcessString4() throws Exception{
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        String tsdString = null;
        
        Map<String,String> dataLabels = new HashMap<>();
        Map<String,Point2D> dataPoints = new HashMap<>();
        
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }
    
    private String checkedname(String name) throws TSDProcessor.InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new TSDProcessor.InvalidDataNameException(name);
        return name;
    }
    
}
