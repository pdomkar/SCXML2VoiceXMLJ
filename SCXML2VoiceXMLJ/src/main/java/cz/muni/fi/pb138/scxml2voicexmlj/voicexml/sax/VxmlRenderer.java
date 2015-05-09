/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.sax;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VxmlRenderer {

    private DocumentBuilder docBuilder;
    private Transformer transformer;

    public VxmlRenderer() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public String render(Iterable<StateModel> model) {
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("vxml");
        doc.appendChild(root);

        return output(doc);
    }

    private String output(Document doc) {
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        try {
            transformer.transform(source, result);
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException | TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

}
