/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Petr Mejzlik
 */
public class SrgsImpl implements Srgs {
    public Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileNamePrefix) {
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
        
        int inlineGrammarNum = 1;

        // get the grammar reference for the file 
        result.put(null, null);
        NodeList scxmlElems = doc.getElementsByTagName("scxml");
        Element scxml = (Element) scxmlElems.item(0);
        NodeList scxmlDatamodelElems = scxml.getElementsByTagName("datamodel");
        Element scxmlDatamodel = null;
        for (int i = 0; i < scxmlDatamodelElems.getLength(); i++) {
            if (scxmlDatamodelElems.item(i).getParentNode().getNodeName().equals("scxml")) 
                scxmlDatamodel = (Element) scxmlDatamodelElems.item(i);
        }
        if (scxmlDatamodel != null) {
            NodeList dataElems = scxmlDatamodel.getElementsByTagName("data");
            if (dataElems.getLength() == 1) {  // TODO: what to do if there is more than 1 <data> element?
                Node dataNode = dataElems.item(0);
                Element data = (Element) dataNode;
                if (data.hasAttribute("expr")) {
                    result.put(null, data.getAttribute("expr"));
                } else {
                    // an inline grammar inside of the <data> element
                    NodeList grammarElems = data.getElementsByTagName("grammar");
                    if (grammarElems.getLength() == 1) { 
                        Node grammarNode = grammarElems.item(0);
                        Element grammar = (Element) grammarNode;

                        // create a trasformer for writing the grammar to a string
                        Transformer transformer = null;
                        try {
                            transformer = TransformerFactory.newInstance().newTransformer();
                            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // so that the output grxml file is nicely formatted 
                            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // we are generating an inline grammar for vxml, 
                                                                                                    // so we don't want an XML declaration
                        } catch (TransformerConfigurationException ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        // create the grammar document
                        DocumentBuilder builder = null;
                        Document grammarDoc = null;
                        try {
                            builder = factory.newDocumentBuilder();
                            grammarDoc = builder.newDocument();
                        } catch (ParserConfigurationException ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        grammarDoc.appendChild(grammarDoc.importNode(grammarNode, true));

                        // transform the grammar into the string writer
                        DOMSource grammarNodeDOMSource = new DOMSource(grammarDoc);
                        StringWriter writer = new StringWriter();
                        try {
                            transformer.transform(grammarNodeDOMSource, new StreamResult(writer));
                        } catch (TransformerException ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            writer.close();
                        } catch (IOException ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        // get the string from the string writer 
                        String grammarStr = writer.getBuffer().toString();

                        // store the grammar code in a file
                        String grxmlFileName = grxmlFileNamePrefix + inlineGrammarNum + ".grxml"; inlineGrammarNum++;

                        PrintWriter printWriter = null;
                        try {
                            printWriter = new PrintWriter(new File(grxmlFileName));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        printWriter.println(grammarStr);
                        printWriter.close();

                        // assign the state the code to link to its grammar in VoiceXML
                        result.put(null, grxmlFileName + "#" + grammar.getAttribute("root"));
                    } else {
                        try {
                            throw new Exception("there isn't exactly one <grammar> element in <data>");
                        } catch (Exception ex) {
                            Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } 
                }
            } else {
                try {
                    throw new Exception("there isn't exactly one <data> element in <datamodel>, this is not supported");
                } catch (Exception ex) {
                    Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        
        // get grammar references for the states 
        NodeList stateElems = doc.getElementsByTagName("state");
        for (int i = 0; i < stateElems.getLength(); i++) {
            Element state = (Element) stateElems.item(i);
            NodeList datamodelElems = state.getElementsByTagName("datamodel");
            if (datamodelElems.getLength() == 1) {
                Element datamodel = (Element) datamodelElems.item(0);
                NodeList dataElems = datamodel.getElementsByTagName("data");
                if (dataElems.getLength() == 1) {  // TODO: what to do if there is more than 1 <data> element?
                    Node dataNode = dataElems.item(0);
                    Element data = (Element) dataNode;
                    if (data.hasAttribute("expr")) {
                        result.put(state.getAttribute("id"), data.getAttribute("expr"));
                    } else {
                        // an inline grammar inside of the <data> element
                        NodeList grammarElems = data.getElementsByTagName("grammar");
                        if (grammarElems.getLength() == 1) { 
                            Node grammarNode = grammarElems.item(0);
                            Element grammar = (Element) grammarNode;
                            
                            // adapt the grammar element for use in VoiceXML interpreter
                            grammar.setAttribute("version", "1.0");
                            grammar.setAttribute("mode", "voice");
                            
                            // create a trasformer for writing the grammar to a string
                            Transformer transformer = null;
                            try {
                                transformer = TransformerFactory.newInstance().newTransformer();
                                //transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // so that the output grxml file is nicely formatted 
                                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // we are generating an inline grammar for vxml, 
                                                                                                        // so we don't want an XML declaration
                            } catch (TransformerConfigurationException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            // create the grammar document
                            DocumentBuilder builder = null;
                            Document grammarDoc = null;
                            try {
                                builder = factory.newDocumentBuilder();
                                grammarDoc = builder.newDocument();
                            } catch (ParserConfigurationException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            grammarDoc.appendChild(grammarDoc.importNode(grammarNode, true));
                            
                            // transform the grammar into the string writer
                            DOMSource grammarNodeDOMSource = new DOMSource(grammarDoc);
                            StringWriter writer = new StringWriter();
                            try {
                                transformer.transform(grammarNodeDOMSource, new StreamResult(writer));
                            } catch (TransformerException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                writer.close();
                            } catch (IOException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            // get the string from the string writer 
                            String grammarStr = writer.getBuffer().toString();
                            
                            // store the grammar code in a file
                            String grxmlFileName = grxmlFileNamePrefix + inlineGrammarNum + ".grxml"; inlineGrammarNum++;
                            
                            PrintWriter printWriter = null;
                            try {
                                printWriter = new PrintWriter(new File(grxmlFileName));
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            printWriter.println(grammarStr);
                            printWriter.close();
                            
                            // assign the state the code to link to its grammar in VoiceXML
                            result.put(state.getAttribute("id"), grxmlFileName + "#" + grammar.getAttribute("root"));
                        } else {
                            try {
                                throw new Exception("there isn't exactly one <grammar> element in <data>");
                            } catch (Exception ex) {
                                Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } 
                    }
                } else {
                    try {
                        throw new Exception("there isn't exactly one <data> element in <datamodel>, this is not supported");
                    } catch (Exception ex) {
                        Logger.getLogger(SrgsImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return result;
    }
    
}
