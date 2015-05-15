/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt.XsltBasedScxmlToVoicexmlConverter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XsltBasedScxmlToVoicexmlConverterTest {

    @Test
    public void testRunSimpleConversion() {
        InputStream stylesheet = getClass().getResourceAsStream("/stylesheetHello.xsl");
        InputStream toBeConverted = getClass().getResourceAsStream("/AccountType.scxml");
        XsltBasedScxmlToVoicexmlConverter conv = new XsltBasedScxmlToVoicexmlConverter(stylesheet);
        String result = conv.convert(toBeConverted, null);
        assertThat(result).contains("Hello");
    }

    @Test
    public void testRender() throws ParserConfigurationException, TransformerException {
        XsltBasedScxmlToVoicexmlConverter conv = new XsltBasedScxmlToVoicexmlConverter(null);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("root");
        root.setAttribute("att", "value");
        Element child = doc.createElement("child");
        root.appendChild(child);
        doc.appendChild(root);
        String result = conv.render(doc);
        Pattern pattern = Pattern.compile(".*?<root att=\"value\">.*?child.*?<\\/root>", Pattern.DOTALL);
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void testGrammarReferenceAdded() throws ParserConfigurationException, TransformerException, XPathExpressionException {
        XsltBasedScxmlToVoicexmlConverter conv = new XsltBasedScxmlToVoicexmlConverter(null);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("root");
        Element state = doc.createElement("field");
        state.setAttribute("name", "someField");
        root.appendChild(state);
        doc.appendChild(root);
        Map<String, String> grammarReferences = new HashMap<>();
        grammarReferences.put("someField", "grammar");
        conv.appendGrammarReferences(doc, grammarReferences);
        String result = conv.render(doc);
        Pattern pattern = Pattern.compile(".*?<field name=\"someField\">.*?<grammar src=\"grammar\" type=\"application\\/grammar\\+xml\"\\/>.*", Pattern.DOTALL);
        assertTrue(pattern.matcher(result).matches());
    }

}
