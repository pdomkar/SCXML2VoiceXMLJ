/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

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
    public String convert(InputStream scxmlContent, Map<String, String> srgsReferences) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer.transform(new StreamSource(scxmlContent), new DOMResult(document));
            appendGrammarReferences(document, srgsReferences);
            return render(document);

        } catch (TransformerException | ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private void appendGrammarReferences(Document doc, Map<String, String> srgsReferences) {

    }

    private String render(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        return xmlString;
    }

}
