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
    public void testGetSrgsReferences() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        InputStream in = new FileInputStream("src/test/resources/Registration.scxml");
        Map<String,String> correctReferences = new HashMap();
        correctReferences.put("Grammar", "<grammar src=\"registrace.grxml\"/>");
        correctReferences.put("Finishing", "<grammar src=\"./registration.grxml#ukonceni\"/>");
        correctReferences.put("Course", "<grammar src=\"./registration.grxml#predmet\"/>");
        correctReferences.put("All", "<grammar src=\"./registration.grxml#anone\"/>");
        
        Srgs srgs = new SrgsImpl();
        Map<String,String> references = srgs.getSrgsReferences(in);
        if (!references.equals(correctReferences)) fail();
    }
}
