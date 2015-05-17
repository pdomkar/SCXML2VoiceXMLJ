/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomXpathBasedScxmlToVoicexmlConverterTest {

    private DomXpathBasedScxmlToVoicexmlConverter conv;

    @Before
    public void setup() {
        conv = new DomXpathBasedScxmlToVoicexmlConverter();
    }

    @Test
    public void testFindStates() throws Exception {
        Document doc = parseFile("/Registration.scxml");
        List<Element> states = conv.findStates(doc);
        assertThat(extractAttributes(states, "id")).containsOnly("Start", "Finishing", "Course", "All", "Submit");
    }

    @Test
    public void testNoScxmlElement() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        try {
            conv.startStateName(doc);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("root");
        }
    }

    @Test
    public void testNoInitialAttribute() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element scxml = doc.createElement("scxml");
        doc.appendChild(scxml);
        try {
            conv.startStateName(doc);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("initial");
        }
    }

    @Test
    public void testExtractTransitions() {
        Document doc = parseFile("/Registration.scxml");
        Element state = conv.executeXpathSingleElement(doc, "//state[@id='Start']");
        List<String> transitionTargets = conv.extractTransitions(state);
        assertThat(transitionTargets).containsOnly("All", "Finishing", "Course");
    }

    @Test
    public void testExtractTransitionsGraph() {
        Document doc = parseFile("/Registration.scxml");
        Collection<GraphNode> nodes = conv.extractTransitionsGraph(doc, "Start");
        Map<String, GraphNode> nodesByName = new HashMap<>();
        for (GraphNode node : nodes) {
            nodesByName.put(node.name(), node);
        }
        assertThat(nodes).hasSize(5);
        assertThat(nodesByName.get("Start").allPaths()).containsOnly(
                nodesByName.get("All"), nodesByName.get("Finishing"), nodesByName.get("Course"));
        assertThat(nodesByName.get("Finishing").allPaths()).containsOnly(
                nodesByName.get("All"));
        assertThat(nodesByName.get("Course").allPaths()).containsOnly(
                nodesByName.get("All"));
        assertThat(nodesByName.get("All").allPaths()).containsOnly(
                nodesByName.get("Submit"));
        assertThat(nodesByName.get("Submit").allPaths()).hasSize(0);
    }

    @Test
    public void testConvertSingleState() {
        GrammarReference grammar = mock(GrammarReference.class);
        when(grammar.grammarFile()).thenReturn("registrace.grxml");
        when(grammar.referenceForState("Course")).thenReturn("./registration.grxml#predmet");
        String vxmlActual = conv.convert(getClass().getResourceAsStream("/Registration_singleState.scxml"), grammar);
        String vxmlExpected = conv.render(parseFile("/Registration_singleState.vxml"));
        System.out.println(vxmlActual.replaceAll("\\s+", ""));
        System.out.println(vxmlExpected.replaceAll("\\s+", ""));
        assertThat(vxmlActual).isEqualTo(vxmlExpected);
    }

    @Test
    public void testPerformTransformationOnState() {
        Document scxml = parseFile("/Registration_singleState.scxml");
        Document vxml = parseFile("/vxmlTemplate.xml");
        Element form = conv.executeXpathSingleElement(vxml, "//form");
        Element state = conv.executeXpathSingleElement(scxml, "//state");
        conv.appendTransformedState(form, state, "/stylesheetHello.xsl");
        String result = conv.render(vxml).replaceAll("[\\r\\n]+", "");
        assertThat(result).matches(".*<form id=\"main\">\\s*<Hello/>\\s*</form>.*");
    }

    @Test
    public void testAppendingStatesRetainsOrdering() {
        Document scxml = parseFile("/Registration_singleState.scxml");
        Document vxml = parseFile("/vxmlTemplate.xml");
        Element form = conv.executeXpathSingleElement(vxml, "//form");
        Element state = conv.executeXpathSingleElement(scxml, "//state");
        conv.appendTransformedState(form, state, "/stylesheetHello.xsl");
        conv.appendTransformedState(form, state, "/stylesheetGoodbye.xsl");
        String result = conv.render(vxml).replaceAll("[\\r\\n]+", "");
        assertThat(result).matches(".*<form id=\"main\">\\s*<Hello/>s*<Goodbye/>\\s*</form>.*");
    }

    private Document parseFile(String name) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(getClass().getResourceAsStream(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> extractAttributes(List<Element> elements, String name) {
        List<String> result = new ArrayList<>();
        for (Element e : elements) {
            result.add(conv.extractAttribute(e, name));
        }
        return result;
    }
}
