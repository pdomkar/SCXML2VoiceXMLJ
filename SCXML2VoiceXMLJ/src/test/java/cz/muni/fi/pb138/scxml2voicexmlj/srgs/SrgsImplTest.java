/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import org.custommonkey.xmlunit.Diff;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

/**
 *
 * @author Petr Mejzlik
 */
public class SrgsImplTest {
    @Test
    public void testGetSrgsReferencesOnRegistrationScxml() throws FileNotFoundException {
        InputStream in = new FileInputStream("src/test/resources/Registration.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put(null, "./registrace.grxml"); // grammar from the <datamodel> of the whole file (no state)
        correctReferences.put("Finishing", "./registration.grxml#ukonceni");
        correctReferences.put("Course", "./registration.grxml#predmet");
        correctReferences.put("All", "./registration.grxml#anone");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration-GENERATED_GRAMMAR_");
        if (!references.equals(correctReferences)) fail();
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineOneRule() throws FileNotFoundException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineOneRule.scxml");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_");
        
        if (!references.get(null).equals("./registrace.grxml")) fail(); // grammar from the <datamodel> of the whole file (no state)
        
        if (!references.get("Finishing").equals("./registration.grxml#ukonceni")) fail();
        
        if (!references.get("Course").equals("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_1.grxml#predmet")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_1.grxml"), 
                "<grammar mode=\"voice\" root=\"predmet\" version=\"1.0\">\n" +
"                <rule id=\"predmet\">\n" +
"                    <one-of>\n" +
"                        <item>pb095<tag>{$.predmet = 'pb095';}</tag></item>\n" +
"                        <item>pb125<tag>{$.predmet = 'pb125';}</tag></item>\n" +
"                        <item>pb162<tag>{$.predmet = 'pb162';}</tag></item>\n" +
"                        <item>pa156<tag>{$.predmet = 'pa156';}</tag></item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
        if (!references.get("All").equals("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_2.grxml#anone")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineOneRule-GENERATED_GRAMMAR_2.grxml"), 
                "<grammar mode=\"voice\" root=\"anone\" version=\"1.0\">\n" +
"                <rule id=\"anone\">\n" +
"                    <one-of>\n" +
"                        <item>ano</item>\n" +
"                        <item>ne</item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_inlineMultipleRules() throws FileNotFoundException {
        InputStream in = new FileInputStream("src/test/resources/Registration_inlineMultipleRules.scxml");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in, "src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_");
        
        if (!references.get(null).equals("./registrace.grxml")) fail(); // grammar from the <datamodel> of the whole file (no state)
        
        if (!references.get("Finishing").equals("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_1.grxml#ukonceni")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_1.grxml"), 
                "<grammar mode=\"voice\" root=\"ukonceni\" version=\"1.0\">\n" +
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
        
        if (!references.get("Course").equals("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_2.grxml#predmet")) fail();
        if (!xmlIdentical(readFile("src/test/resources/Registration_inlineMultipleRules-GENERATED_GRAMMAR_2.grxml"), 
                "<grammar mode=\"voice\" root=\"predmet\" version=\"1.0\">\n" +
"                <rule id=\"predmet\">\n" +
"                    <one-of>\n" +
"                        <item>pb095<tag>{$.predmet = 'pb095';}</tag></item>\n" +
"                        <item>pb125<tag>{$.predmet = 'pb125';}</tag></item>\n" +
"                        <item>pb162<tag>{$.predmet = 'pb162';}</tag></item>\n" +
"                        <item>pa156<tag>{$.predmet = 'pa156';}</tag></item>\n" +
"                    </one-of>\n" +
"                </rule>\n" +
"               </grammar>")) fail();
        
        if (!references.get("All").equals("./registration.grxml#anone")) fail();
        
    }
    
    
    @Test
    public void testGetSrgsReferencesOnRegistration_singleStateScxml() throws FileNotFoundException {
        InputStream in = new FileInputStream("src/test/resources/Registration_singleState.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put(null, null); // grammar from the <datamodel> of the whole file (no state) - in this scxml file there is none
        correctReferences.put("Course", "./registration.grxml#predmet");
        
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
