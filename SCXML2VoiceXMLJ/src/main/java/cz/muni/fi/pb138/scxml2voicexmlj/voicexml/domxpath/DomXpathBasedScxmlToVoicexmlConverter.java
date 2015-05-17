/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomXpathBasedScxmlToVoicexmlConverter implements ScxmlToVoicexmlConverter {

    private XPath xpath;
    private DocumentBuilder docBuilder;

    public DomXpathBasedScxmlToVoicexmlConverter() {
        try {
            xpath = XPathFactory.newInstance().newXPath();
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        try {
            Document scxml = docBuilder.parse(scxmlContent);
            Document vxml = docBuilder.parse(getClass().getResourceAsStream("/vxmlTemplate.xml"));
            String initialState = startStateName(scxml);
            Collection<GraphNode> rawNodes = extractTransitionsGraph(scxml, initialState);
            List<GraphNode> orderedNodes = GraphHelper.orderedTopologically(rawNodes);
            appendGrammarFile(vxml, srgsReferences);
            List<Element> orderedStates = extractStatesOrdered(scxml, orderedNodes);
            Element form = executeXpathSingleElement(vxml, "//form");
            for (Element state : orderedStates) {
                Element transformedField = transformElementAndAdoptBy(state, "/scxmlToVxml.xsl", vxml);
                appendGrammarField(transformedField, srgsReferences);
                form.appendChild(transformedField);
            }
            return render(vxml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Element transformElementAndAdoptBy(Element source, String transformation, Document parent) {
        Element rawTransformed = transformElement(source, transformation);
        Element adoptedField = (Element) rawTransformed.cloneNode(true);
        parent.adoptNode(adoptedField);
        return adoptedField;
    }

    public Element transformElement(Element source, String transformation) {
        try (InputStream stylesheet = getClass().getResourceAsStream(transformation)) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet));
            DOMResult result = new DOMResult();
            transformer.transform(new DOMSource(source), result);
            return (Element) result.getNode().getFirstChild();
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Element> extractStatesOrdered(Document scxml, List<GraphNode> orderedNodes) {
        List<Element> states = new ArrayList<>();
        for (GraphNode node : orderedNodes) {
            states.add(executeXpathSingleElement(scxml, "//state[@id='" + node.name() + "']"));
        }
        return states;
    }

    public void appendGrammarFile(Document document, GrammarReference srgsReferences) {
        Element form = executeXpathSingleElement(document, "//form");
        Element grammar = document.createElement("grammar");
        grammar.setAttribute("src", srgsReferences.grammarFile());
        form.appendChild(grammar);
    }

    public void appendGrammarField(Element field, GrammarReference srgsReferences) {
        String name = field.getAttribute("name");
        if (!srgsReferences.stateHasGrammarReference(name)) {
            return;
        }
        String reference = srgsReferences.referenceForState(name);
        Element grammar = field.getOwnerDocument().createElement("grammar");
        grammar.setAttribute("src", reference);
        Element nomatch = executeXpathSingleElement(field, "./*[name()='nomatch']");
        field.insertBefore(grammar, nomatch);
    }

    public String render(Node domTree) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(domTree);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            return xmlString;
        } catch (TransformerException | TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<GraphNode> extractTransitionsGraph(Document document, String initialState) {
        Map<String, GraphNode> graphsById = new HashMap<>();
        List<Element> states = findStates(document);
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
                if (transitionTargetName.equals(initialState)) {
                    continue;
                }
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
        Element scxml;
        try {
            scxml = executeXpathSingleElement(doc, "/scxml");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No root scxml element", e);
        }
        String start = scxml.getAttribute("initial");
        if (start.isEmpty()) {
            throw new IllegalArgumentException("initial state not declared: " + scxml);
        }
        return start;
    }

    public NodeList executeXpath(Node root, String query) {
        try {
            XPathExpression expr = xpath.compile(query);
            return (NodeList) expr.evaluate(root, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public Element executeXpathSingleElement(Node root, String query) {
        List<Element> selected = toElementList(executeXpath(root, query));
        if (selected.size() != 1) {
            throw new IllegalArgumentException("Exactly one element must match the query " + query
                    + " to extract the one element, got " + selected);
        }
        return selected.get(0);
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
