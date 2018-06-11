/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ricky1999
 */
public class ConfigTest {
    
    public ConfigTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getChart method, of class AppUI.
     */
    
    /*
        configDialog does many things, one of which is to make sure the test values for iteration, interval, and clusterNum are valid
        Boundary cases for each of these values must be tested:
        *Case where all values are valid: (100,10,3)
        *Case where interval value is on the boundary: (100,1,3)
        *Case where iteration value is on the boundary: (1,10,3)
        *Case where clusterNum is on the lower bound: (100,10,2)
        *Case where clusterNum is on the upper bound: (100,10,4)
    */
    
    @Test
    public void configDialogTest(){
        int[] settings = new int[4];
        
        String iterations = "100";
        String interval = "10";
        String clusterNum = "3";
        
        parseSettings(settings, iterations, 0, 1, Integer.MAX_VALUE);
        parseSettings(settings, interval, 1, 1, Integer.MAX_VALUE);
        parseSettings(settings, clusterNum, 2, 2, 4);
        
        assertEquals(settings[0],100);
        assertEquals(settings[1],10);
        assertEquals(settings[2],3); 
    }
    
    @Test
    public void configDialogTest2(){
        int[] settings = new int[4];
        
        String iterations = "100";
        String interval = "1";
        String clusterNum = "3";
        
        parseSettings(settings, iterations, 0, 1, Integer.MAX_VALUE);
        parseSettings(settings, interval, 1, 1, Integer.MAX_VALUE);
        parseSettings(settings, clusterNum, 2, 2, 4);
        
        assertEquals(settings[0],100);
        assertEquals(settings[1],1);
        assertEquals(settings[2],3);  
    }
    
    @Test
    public void configDialogTest3(){
        int[] settings = new int[4];
        
        String iterations = "1";
        String interval = "10";
        String clusterNum = "3";
        
        parseSettings(settings, iterations, 0, 1, Integer.MAX_VALUE);
        parseSettings(settings, interval, 1, 1, Integer.MAX_VALUE);
        parseSettings(settings, clusterNum, 2, 2, 4);
        
        assertEquals(settings[0],1);
        assertEquals(settings[1],10);
        assertEquals(settings[2],3);   
    }
    
    @Test
    public void configDialogTest4(){
        int[] settings = new int[4];
        
        String iterations = "100";
        String interval = "10";
        String clusterNum = "2";
        
        parseSettings(settings, iterations, 0, 1, Integer.MAX_VALUE);
        parseSettings(settings, interval, 1, 1, Integer.MAX_VALUE);
        parseSettings(settings, clusterNum, 2, 2, 4);
        
        assertEquals(settings[0],100);
        assertEquals(settings[1],10);
        assertEquals(settings[2],2); 
    }
    
    @Test
    public void configDialogTest5(){
        int[] settings = new int[4];
        
        String iterations = "100";
        String interval = "10";
        String clusterNum = "4";
        
        parseSettings(settings, iterations, 0, 1, Integer.MAX_VALUE);
        parseSettings(settings, interval, 1, 1, Integer.MAX_VALUE);
        parseSettings(settings, clusterNum, 2, 2, 4);
        
        assertEquals(settings[0],100);
        assertEquals(settings[1],10);
        assertEquals(settings[2],4);
    }  
    
    private int parseString(String s, int minDefaultValue, int maxDefaultValue) throws NumberFormatException {
        int a =Integer.parseInt(s);
        if (a>=minDefaultValue && a<=maxDefaultValue){return a;}
        else return minDefaultValue;
    }
    
    private void parseSettings(int[] settings, String userInput, int index, int minDefaultValue,int maxDefaultValue) {
        try {
            settings[index] = parseString(userInput, minDefaultValue, maxDefaultValue);
        } catch (NumberFormatException e) {
            settings[index] = minDefaultValue;
        }
    }
    
}
