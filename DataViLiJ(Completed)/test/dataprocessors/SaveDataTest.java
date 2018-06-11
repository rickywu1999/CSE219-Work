/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ricky1999
 */
public class SaveDataTest {
    
    public SaveDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /*
        Since the method takes a Path, there are only a few cases to test:
        *The first test is for a valid path name
        *The second test is for an invalid path name, which should return a NullPointerException
    */
    @Test
    public void testSaveData() throws IOException{
        System.out.println("saveData Test");
        Path dataFilePath = Paths.get("data-vilij/resources/data/testdata.tsd");
        FileWriter fileWriter;
        String info = "@1\t1\t1,1";
        fileWriter = new FileWriter(dataFilePath.toString());
        fileWriter.write(info);
        fileWriter.close();
        
        Scanner sc = new Scanner(dataFilePath);
        assertEquals(sc.nextLine(), info);
        sc.close();
    }
    
    @Test(expected = NullPointerException.class)
    public void testSaveData2() throws IOException{
        System.out.println("saveData Test");
        Path dataFilePath = Paths.get(null);
        FileWriter fileWriter;
        String info = "@1\t1\t1,1";
        fileWriter = new FileWriter(dataFilePath.toString());
        fileWriter.write(info);
        fileWriter.close();
        
        Scanner sc = new Scanner(dataFilePath);
        assertEquals(sc.nextLine(), info);
    }
    
}
