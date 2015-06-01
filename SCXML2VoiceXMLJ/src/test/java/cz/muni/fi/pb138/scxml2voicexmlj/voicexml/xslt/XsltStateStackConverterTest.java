/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import java.net.URISyntaxException;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public class XsltStateStackConverterTest {

    private XmlHelper helper;
    private XsltStateStackConverter conv;

    @Before
    public void setup() {
        helper = new XmlHelper();
        conv = new XsltStateStackConverter();
    }

    @Test
    public void testForwardTransitionsIgnored() throws ParserConfigurationException, URISyntaxException {
        Document scxml = helper.parseFile("/testForwardReferences.scxml");
        List<Element> states = helper.toElementList(helper.executeXpath(scxml, "//*[local-name()='state']"));
        List<Element> fields = conv.transformStates(states, conv.emptyVxmlDocument());

        Document actual = conv.emptyVxmlDocument();
        Element form = helper.executeXpathSingleElement(actual, "//*[local-name()='form']");
        for (Element field : fields) {
            Element adopted = helper.adoptElement(field, actual);
            form.appendChild(adopted);
        }
        Source actualSource = new DOMSource(actual);
        Document vxml = helper.parseFile("/testForwardReferences.vxml");
        Source vxmlSource = new DOMSource(helper.parseFile("/testForwardReferences.vxml"));
        assertThat(the(actual), isEquivalentTo(the(vxml)));
    }

    public Document parseFile(String name) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(getClass().getResourceAsStream(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBackwardTransitionsConvertedToClear() {

    }

}
