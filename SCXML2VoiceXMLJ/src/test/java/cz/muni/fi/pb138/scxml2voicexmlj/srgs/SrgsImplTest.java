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
        /*InputStream in = new FileInputStream("src/test/resources/Card.vxml");
        Srgs srgs = new SrgsImpl();
        srgs.getSrgsReferences(in, "src/test/resources/Card_inline-gramars.grxml");*/
        // ...TODO
    }
}
