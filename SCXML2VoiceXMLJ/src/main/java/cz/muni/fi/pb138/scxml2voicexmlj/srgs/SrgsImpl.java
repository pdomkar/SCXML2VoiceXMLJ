/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author petr
 */
public class SrgsImpl implements Srgs {
    public Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileName) {
        Map<String,String> result = new HashMap<String,String>();

        //...
        // TODO: SCXML -> VoiceXML
        
        /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        Document doc = null;
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            StreamSource source = new StreamSource(voicexmlContent);
            doc = builder.parse(source.getInputStream());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
        NodeList grammarElems = doc.getElementsByTagName("grammar");
        for (int i = 0; i < grammarElems.getLength(); i++) {
            Element grammar = (Element) grammarElems.item(i);
            if (grammar.hasAttribute("src") && !grammar.getAttribute("src").equals("")) {
                // TODO
            }
        }*/
        
        return result;
    }
}
