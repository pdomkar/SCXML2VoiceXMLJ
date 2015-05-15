/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DomXpathBasedScxmlToVoicexmlConverterTest {

    private DomXpathBasedScxmlToVoicexmlConverter conv;

    @Before
    public void setup() {
        conv = new DomXpathBasedScxmlToVoicexmlConverter();
    }

    @Test
    public void testFindStates() throws Exception {
        Document doc = parseRegistrationScxmlFile();
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
        Document doc = parseRegistrationScxmlFile();
        NodeList stateList = conv.executeXpath(doc, "//state[@id='Start']");
        Element state = conv.toElementList(stateList).get(0);
        List<String> transitionTargets = conv.extractTransitions(state);
        assertThat(transitionTargets).containsOnly("All", "Finishing", "Course");
    }

    @Test
    public void testExtractTransitionsGraph() {
        Document doc = parseRegistrationScxmlFile();
        Collection<GraphNode> nodes = conv.extractTransitionsGraph(doc);
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
                nodesByName.get("Start"), nodesByName.get("Submit"));
        assertThat(nodesByName.get("Submit").allPaths()).hasSize(0);
    }

    private Document parseRegistrationScxmlFile() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(getClass().getResourceAsStream("/Registration.scxml"));
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
