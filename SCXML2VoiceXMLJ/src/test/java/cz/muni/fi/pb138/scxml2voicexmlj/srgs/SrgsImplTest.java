/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author petr
 */
public class SrgsImplTest {
    @Test
    public void testGetSrgsReferencesOnRegistrationScxml() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration.scxml");
        Map<String,String> correctReferences = new HashMap();
        //correctReferences.put("Grammar", "<grammar src=\"registrace.grxml\"/>"); // grammar from the <datamodel> of the whole file (no state) 
        correctReferences.put("Finishing", "<grammar src=\"./registration.grxml#ukonceni\"/>");
        correctReferences.put("Course", "<grammar src=\"./registration.grxml#predmet\"/>");
        correctReferences.put("All", "<grammar src=\"./registration.grxml#anone\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration-GENERATED_GRAMMARS.grxml");
        if (!references.equals(correctReferences)) fail();
    }
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineOneRule() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineOneRule.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put("Finishing", "<grammar src=\"./registration.grxml#ukonceni\"/>");
        correctReferences.put("Course", "<grammar src=\"src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMARS.grxml#predmet\"/>");
        correctReferences.put("All", "<grammar src=\"src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMARS.grxml#anone\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMARS.grxml");
        if (!references.equals(correctReferences)) fail();
        // NOTE: this scxml file contains inline grammars, manually check if the grxml file is generated correctly 
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineMultipleRules() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineMultipleRules.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put("Finishing", "<grammar src=\"./Registration_inlineMultipleRules-GENERATED_GRAMMARS.grxml#ukonceni\"/>");  // <- problem: based on what to choose #ukonceni?
        correctReferences.put("Course", "<grammar src=\"src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMARS.grxml#predmet\"/>");
        correctReferences.put("All", "<grammar src=\"./registration.grxml#anone\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMARS.grxml");
        if (!references.equals(correctReferences)) fail(); 
        // NOTE: this scxml file contains inline grammars, manually check if the grxml file is generated correctly
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_singleStateScxml() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_singleState.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put("Course", "<grammar src=\"./registration.grxml#predmet\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_singleState-GENERATED_GRAMMARS.grxml");
        if (!references.equals(correctReferences)) fail();
    }
}
