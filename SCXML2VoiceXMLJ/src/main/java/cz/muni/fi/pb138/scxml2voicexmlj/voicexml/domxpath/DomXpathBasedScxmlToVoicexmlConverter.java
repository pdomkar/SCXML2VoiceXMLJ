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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomXpathBasedScxmlToVoicexmlConverter {

    private XPath xpath = XPathFactory.newInstance().newXPath();

    public Collection<GraphNode> extractTransitionsGraph(Document document) {
        Map<String, GraphNode> graphsById = new HashMap<>();
        List<Element> states = findStates(document);
        String initialState = startStateName(document);
        for (Element state : states) {
            String name = extractAttribute(state, "id");
            graphsById.put(extractStateId(state), new GraphNode(name));
        }
        //we have to be sure that the state we reference is already in the map,
        //thus all states are created first and then we can append transitions info
        for (Element state : states) {
            String name = extractAttribute(state, "id");
            GraphNode current = graphsById.get(name);
            for (String transitionTargetName : extractTransitions(state)) {
                GraphNode target = graphsById.get(transitionTargetName);
                current.addPathTo(target);
            }
        }
        return graphsById.values();
    }

    private String extractStateId(Element state) {
        String id = state.getAttribute("id");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("State doesnt have id: " + state);
        }
        return id;
    }

    public List<String> extractTransitions(Element state) {
        List<String> result = new ArrayList<>();
        for (Element transition : toElementList(executeXpath(state, "transition"))) {
            String target = transition.getAttribute("target");
            if (target.isEmpty()) {
                throw new IllegalArgumentException("transition without target: " + transition);
            }
            result.add(target);
        }
        return result;
    }

    public List<Element> findStates(Document doc) {
        return toElementList(executeXpath(doc, "//*[self::state|self::final]"));
    }

    public String startStateName(Document doc) {
        NodeList scxmlList = executeXpath(doc, "/scxml");
        Element scxml = (Element) scxmlList.item(0);
        if (scxml == null) {
            throw new IllegalArgumentException("No root scxml element");
        }
        String start = scxml.getAttribute("initial");
        if (start.isEmpty()) {
            throw new IllegalArgumentException("initial state not declared: " + scxml);
        }
        return start;
    }

    public NodeList executeXpath(Node node, String query) {
        try {
            XPathExpression expr = xpath.compile(query);
            return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Element> toElementList(NodeList nodelist) {
        List<Element> elementList = new ArrayList<>(nodelist.getLength());
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node instanceof Element) {
                elementList.add((Element) nodelist.item(i));
            }
        }
        return elementList;
    }

    public String extractAttribute(Element element, String name) {
        String attribute = element.getAttribute(name);
        if (attribute.isEmpty()) {
            throw new IllegalArgumentException("Element " + element + " doesnt have attribute " + name);
        }
        return attribute;
    }

}
