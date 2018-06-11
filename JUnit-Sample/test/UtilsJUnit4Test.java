/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import sample.Utils;

/**
 *
 * @author ricky1999
 */
public class UtilsJUnit4Test {
    
    public UtilsJUnit4Test() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("* UtilsJUnit4Test: @BeforeClass method");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("* UtilsJUnit4Test: @AfterClass method");
    }

    @Before
    public void setUp() {
        System.out.println("* UtilsJUnit4Test: @Before method");
    }

    @After
    public void tearDown() {
        System.out.println("* UtilsJUnit4Test: @After method");
    }

    /**
     * Test of concatWords method, of class Utils.
     */
    @Test
    public void helloWorldCheck() {
        System.out.println("* UtilsJUnit4Test: test method 1 - helloWorldCheck()");
        assertEquals("Hello, world!", Utils.concatWords("Hello", ", ", "world", "!"));
    }

    /**
     * Test of computeFactorial method, of class Utils.
     */
    @Test(timeout=1000)
    public void testWithTimeout() {
        System.out.println("* UtilsJUnit4Test: test method 2 - testWithTimeout()");
        final int factorialOf = 1 + (int) (30000 * Math.random());
        System.out.println("computing " + factorialOf + '!');
        System.out.println(factorialOf + "! = " + Utils.computeFactorial(factorialOf));
    }

    /**
     * Test of normalizeWord method, of class Utils.
     */
    @Test
    public void testNormalizeWord() {
        System.out.println("normalizeWord");
        String word = "";
        String expResult = "";
        String result = Utils.normalizeWord(word);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void checkExpectedException() {
        System.out.println("* UtilsJUnit4Test: test method 3 - checkExpectedException()");
        final int factorialOf = -5;
        System.out.println(factorialOf + "! = " + Utils.computeFactorial(factorialOf));
    }
    
    @Ignore
    @Test
    public void temporarilyDisabledTest() throws Exception {
        System.out.println("* UtilsJUnit4Test: test method 4 - checkExpectedException()");
        assertEquals("Malm\u00f6", Utils.normalizeWord("Malmo\u0308"));
    }
    
}
