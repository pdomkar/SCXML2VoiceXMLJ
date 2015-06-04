/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author petr
 */
public class SrgsImpl implements Srgs {
    public Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileName) {
        Map<String,String> result = new HashMap<String,String>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        Document doc = null;
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            StreamSource source = new StreamSource(scxmlContent);
            doc = builder.parse(source.getInputStream());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
        OutputStreamWriter grxmlFileWriter = null;
        Transformer transformer = null;
        boolean grxmlFileUsed = false;  // If there are no inline grammars in the input scxml file 
                                        // the output grxml file will not be created.
        DocumentBuilder builder = null;
        Document rulesDoc = null;
        
        NodeList stateElems = doc.getElementsByTagName("state");
        for (int i = 0; i < stateElems.getLength(); i++) {
            Element state = (Element) stateElems.item(i);
            NodeList datamodelElems = state.getElementsByTagName("datamodel");
            if (datamodelElems.getLength() == 1) {
                Element datamodel = (Element) datamodelElems.item(0);
                NodeList dataElems = datamodel.getElementsByTagName("data");
                if (datamodelElems.getLength() == 1) {  // TODO: what to do if there is more than 1 <data> element?
                    Node dataNode = dataElems.item(0);
                    Element data = (Element) dataNode;
                    if (data.hasAttribute("expr")) {
                        result.put(state.getAttribute("id"), "<grammar src=\"" + data.getAttribute("expr") + "\"/>");
                    } else {
                        
                        Element grammar = null;
                        
                        // inline grammar inside of <data> element
                        if (!grxmlFileUsed) {
                            // open the output grxml file
                            try {
                                grxmlFileWriter = new OutputStreamWriter(new FileOutputStream(grxmlFileName));
                            } catch (FileNotFoundException ex) { 
                                // that file cannot be opened for writing
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            // create a trasformer for writing rules to the output grxml file
                            try {
                                transformer = TransformerFactory.newInstance().newTransformer();
                                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // so that the output grxml file is nicely formatted 
                            } catch (TransformerConfigurationException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            try {
                                builder = factory.newDocumentBuilder();
                                rulesDoc = builder.newDocument();
                            } catch (ParserConfigurationException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            grammar = rulesDoc.createElement("grammar");
                            rulesDoc.appendChild(grammar);
                            //writeGrxmlHeader(grxmlFileWriter);/////
                            grxmlFileUsed = true;
                        }
                        
                        // write the rules inside the <data> element to the grxml output file and reference them 
                        
                        
                        NodeList ruleElems = data.getElementsByTagName("rule");
                        if (ruleElems.getLength() == 1) {
                            Node ruleNode = ruleElems.item(0);
                            Element rule = (Element) ruleNode;
                            
                            rulesDoc.getElementsByTagName("grammar").item(0).appendChild(rulesDoc.importNode(ruleNode, true));
                            
                            
                            result.put(state.getAttribute("id"), "<grammar src=\"" + grxmlFileName + "#" + rule.getAttribute("id") + "\"/>");
                        } else {
                            // TODO: multiple rules (see <data id="Finishing"> in Registration_inlineMultipleRules.scxml) 
                            //          - how to determine which one of them is this state referencing??
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, new Exception("multiple rules not yet implemented"));
                        }
                    }
                }
            }
        }
        
        if (grxmlFileUsed) {
            DOMSource ruleNodeDOMSource = new DOMSource(rulesDoc);
            try {
                transformer.transform(ruleNodeDOMSource, new StreamResult(grxmlFileWriter));
            } catch (TransformerException ex) {
                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            //writeGrxmlFooter(grxmlFileWriter);/////
            try {
                grxmlFileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return result;
    }
    
    private void writeGrxmlHeader(OutputStreamWriter writer) {
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                         "<grammar>");
        } catch (IOException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeGrxmlFooter(OutputStreamWriter writer) {
        try {
            writer.write("</grammar>");
        } catch (IOException ex) {
            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
