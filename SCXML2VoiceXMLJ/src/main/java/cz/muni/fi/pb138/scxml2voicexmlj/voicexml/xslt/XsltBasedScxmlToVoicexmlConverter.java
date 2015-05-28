/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class should be only used once and then thrown away.
 */
public class XsltBasedScxmlToVoicexmlConverter implements ScxmlToVoicexmlConverter {

    private InputStream stylesheet;

    /**
     * The stylesheet stream is not closed in this class, should be closed by client.
     */
    public XsltBasedScxmlToVoicexmlConverter(InputStream stylesheet) {
        this.stylesheet = stylesheet;
    }

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer.transform(new StreamSource(scxmlContent), new DOMResult(document));
            appendGrammarReferences(document, srgsReferences);
            return render(document);

        } catch (TransformerException | ParserConfigurationException | XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    public void appendGrammarReferences(Document doc, GrammarReference srgsReferences) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//field");
        NodeList states = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < states.getLength(); i++) {
            Element field = (Element) states.item(i);
            Element grammar = doc.createElement("grammar");
            String name = field.getAttribute("name");
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Unnamed field, cant assign grammar" + name);
            }
            String reference = srgsReferences.referenceForState(name);
            if (reference == null || reference.isEmpty()) {
                throw new IllegalStateException("Dont know grammar for field " + name);
            }
            grammar.setAttribute("type", "application/grammar+xml");
            grammar.setAttribute("src", reference);
            field.appendChild(grammar);
        }
    }

    public String render(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        return xmlString;
    }

}
