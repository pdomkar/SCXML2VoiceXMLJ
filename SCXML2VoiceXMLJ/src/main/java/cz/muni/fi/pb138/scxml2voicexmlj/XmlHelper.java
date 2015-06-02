/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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

public class XmlHelper {

    private XPath xpath;
    private DocumentBuilder docBuilder;

    public XmlHelper() {
        try {
            xpath = XPathFactory.newInstance().newXPath();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            docBuilder = dbf.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public Element adoptElement(Element source, Document parent) {
        Element adopted = (Element) source.cloneNode(true);
        parent.adoptNode(adopted);
        return adopted;
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

    public Document parseFile(String name) {
        return parseStream(getClass().getResourceAsStream(name));
    }

    public Document parseXmlToDocument(String xml) {
        return parseStream(new ByteArrayInputStream(xml.getBytes()));
    }

    public Element parseXmlToElement(String xml) {
        return executeXpathSingleElement(parseXmlToDocument(xml), "/*");
    }

    public Document parseStream(InputStream xml) {
        try {
            return docBuilder.parse(xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document createDocument() {
        return docBuilder.newDocument();
    }
}
