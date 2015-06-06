/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import org.custommonkey.xmlunit.Diff;
import org.xml.sax.SAXException;

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
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration-GENERATED_GRAMMAR_");
        if (!references.equals(correctReferences)) fail();
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineOneRule() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineOneRule.scxml");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_");
        
        if (!references.get("Finishing").equals("<grammar src=\"./registration.grxml#ukonceni\"/>")) fail();
        
        if (!references.get("Course").equals("<grammar src=\"src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_1.grxml#predmet\"/>")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_1.grxml"), 
                "<grammar root=\"predmet\">\n" +
"                <rule id=\"predmet\">\n" +
"                    <one-of>\n" +
"                        <item>pb095<tag>{$.predmet = 'pb095';}</tag></item>\n" +
"                        <item>pb125<tag>{$.predmet = 'pb125';}</tag></item>\n" +
"                        <item>pb162<tag>{$.predmet = 'pb162';}</tag></item>\n" +
"                        <item>pa156<tag>{$.predmet = 'pa156';}</tag></item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
        if (!references.get("All").equals("<grammar src=\"src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_2.grxml#anone\"/>")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_2.grxml"), 
                "<grammar root=\"anone\">\n" +
"                <rule id=\"anone\">\n" +
"                    <one-of>\n" +
"                        <item>ano</item>\n" +
"                        <item>ne</item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineMultipleRules() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineMultipleRules.scxml");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_");
        
        if (!references.get("Finishing").equals("<grammar src=\"src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_1.grxml#ukonceni\"/>")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_1.grxml"), 
                "<grammar root=\"ukonceni\">\n" +
"                    <rule id=\"ukonceni\">\n" +
"                        <one-of>\n" +
"                            <item tag=\"zkouska\">\n" +
"                                <ruleref uri=\"#zkouska\"/>\n" +
"                            </item>\n" +
"                            <item tag=\"zapocet\">\n" +
"                                <ruleref uri=\"#zapocet\"/>\n" +
"                            </item>\n" +
"                        </one-of>\n" +
"                    </rule>\n" +
"                    <rule id=\"zkouska\">\n" +
"                        <one-of>\n" +
"                            <item>zkouskou</item>\n" +
"                            <item>zkousku</item>\n" +
"                            <item>zkouska</item>\n" +
"                        </one-of>\n" +
"                    </rule>\n" +
"                    <rule id=\"zapocet\">\n" +
"                        <one-of>\n" +
"                            <item>zapoctem</item>\n" +
"                            <item>zapocet</item>\n" +
"                        </one-of>\n" +
"                    </rule>\n" +
"                </grammar>")) fail();
        
        if (!references.get("Course").equals("<grammar src=\"src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_2.grxml#predmet\"/>")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_2.grxml"), 
                "<grammar root=\"predmet\">\n" +
"                <rule id=\"predmet\">\n" +
"                    <one-of>\n" +
"                        <item>pb095<tag>{$.predmet = 'pb095';}</tag></item>\n" +
"                        <item>pb125<tag>{$.predmet = 'pb125';}</tag></item>\n" +
"                        <item>pb162<tag>{$.predmet = 'pb162';}</tag></item>\n" +
"                        <item>pa156<tag>{$.predmet = 'pa156';}</tag></item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
        if (!references.get("All").equals("<grammar src=\"./registration.grxml#anone\"/>")) fail();
        
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_singleStateScxml() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration_singleState.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put("Course", "<grammar src=\"./registration.grxml#predmet\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_singleState-GENERATED_GRAMMAR_");
        if (!references.equals(correctReferences)) fail();
    }
    
    
    private static boolean xmlIdentical(String xml1, String xml2) {
        Diff diff = null;
        try {
            diff = new Diff(xml1, xml2);
        } catch (SAXException ex) {
            Logger.getLogger(SrgsImplTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SrgsImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean isIdentical = diff.identical();
        return isIdentical;
    }
    
    
    private static String readFile(String filename) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SrgsImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        scanner.useDelimiter("\\Z");
        String fileContent = scanner.next();
        scanner.close();
        return fileContent;
    }
}
