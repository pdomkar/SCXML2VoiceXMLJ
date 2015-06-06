/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.cli.MissingArgumentException;
import static org.junit.Assert.fail;
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
        String[] args = {"-i", "src/main/resources/run-data/Registration.scxml", "-s", "src/main/resources/run-data/OUTPUT-grammar_", "-v", "src/main/resources/run-data/OUTPUT-voicexml.vxml"};
        SCXML2VoiceXMLJ.main(args);
    }
    
    /**
     * Test create grammar and voicexml files
     */
    @Test
    public void testCreateFiles() {
        System.out.println("create files");
        String[] args = {"-i", "src/main/resources/run-data/Registration.scxml", "-s", "src/main/resources/run-data/grammar.scxml", "-v", "src/main/resources/run-data/voicexml.scxml"};
        SCXML2VoiceXMLJ.main(args);
       
        try {
           InputStream is = new FileInputStream(new File("src/main/resources/run-data/grammar.scxml"));
        } catch(FileNotFoundException e) {
            fail();
        }
        try {
            InputStream is = new FileInputStream(new File("src/main/resources/run-data/voicexml.scxml"));
         } catch(FileNotFoundException e) {
             fail();
         }
        
    }
    
}
