/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

import org.apache.commons.cli.MissingArgumentException;
import org.junit.Test;

/**
 *
 * @author Petr
 */
public class SCXML2VoiceXMLJTest {
    
    public SCXML2VoiceXMLJTest() {
    }
    

    /**
     * Test of main method
     */
    @Test
    public void testGoodInput() {
        System.out.println("Good input");
        String[] args = {"-i", "src/test/resources/Registration.scxml"};
        SCXML2VoiceXMLJ.main(args);
    }
    
}
