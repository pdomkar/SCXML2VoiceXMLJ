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
    

    /**
     * Test of main method, if there is not too many arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainManyArugments() {
        System.out.println("Many arguments");
        String[] args = {"-i", "first", "-v", "second", "-s", "third", "fourth"};
        SCXML2VoiceXMLJ.main(args);
    }
    
    /**
     * Test of main method, if there is not too little arguments
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainLittleArugments() {
        System.out.println("Little arguments");
        String[] args = {"-i", "first", "v", "second"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"-i", "first"};
        SCXML2VoiceXMLJ.main(args2);
        String[] args3 = {"-i"};
        SCXML2VoiceXMLJ.main(args3);
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
        String[] args = {"-i", "input.txt", "-v", "output.vxml", "-s", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"-i", "input.xml", "-v", "output.vxml", "-s", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
    /**
     * Test of main method, if second argument is *.vxml
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainContentSecondArgument() {
        System.out.println("Test second argument");
        String[] args = {"-i", "input.scxml", "-v", "output.txt", "-s", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"-i", "input.scxml", "-v", "output.xml", "-s", "output2.grxml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
    /**
     * Test of main method, if third argument is *.vxml
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMainContentThirdArgument() {
        System.out.println("Test third argument");
        String[] args = {"-i", "input.scxml", "-v", "output.vxml", "-s", "output2.txt"};
        SCXML2VoiceXMLJ.main(args);
        String[] args2 = {"-i", "input.scxml", "-v",  "output.vxml", "-s", "output2.xml"};
        SCXML2VoiceXMLJ.main(args2);
    }
    
}
