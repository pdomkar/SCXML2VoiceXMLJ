/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr
 */
public class SCXML2VoiceXMLJTest {
    
    public SCXML2VoiceXMLJTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, if there is not too many arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainManyArugments() {
        System.out.println("Many arguments");
        String[] args = {"first", "second", "third", "fourth"};
        SCXML2VoiceXMLJ.main(args);
    }
    
    /**
     * Test of main method, if there is not too little arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainLittleArugments() {
        System.out.println("Little arguments");
        String[] args = {"first", "second"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"first"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
    /**
     * Test of main method, if there is not no argument
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainZeroArugment() {
        System.out.println("Zero arguments");
        String[] args = null;
        SCXML2VoiceXMLJ.main(args);
    }
    
    /**
     * Test of main method, if first argument is *.scxml
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainContentFirstArgument() {
        System.out.println("Test first argument");
        String[] args = {"input.txt", "output.vxml", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"input.xml", "output.vxml", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
    /**
     * Test of main method, if second argument is *.vxml
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainContentSecondArgument() {
        System.out.println("Test second argument");
        String[] args = {"input.scxml", "output.txt", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"input.scxml", "output.xml", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
    /**
     * Test of main method, if third argument is *.vxml
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainContentThirdArgument() {
        System.out.println("Test third argument");
        String[] args = {"input.scxml", "output.vxml", "output2.txt"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"input.scxml", "output.vxml", "output2.xml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
}
