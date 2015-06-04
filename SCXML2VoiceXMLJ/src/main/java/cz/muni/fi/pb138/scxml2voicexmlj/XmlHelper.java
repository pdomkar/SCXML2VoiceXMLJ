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
import static java.util.Arrays.asList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformException;
import org.jdom2.transform.XSLTransformer;
import org.jdom2.xpath.XPathFactory;

public class XmlHelper {

    private XPathFactory xpath;
    private DocumentBuilder docBuilder;

    public XmlHelper() {
        try {
            xpath = XPathFactory.instance();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            docBuilder = dbf.newDocumentBuilder();
            //   docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Element transformElement(Element source, String stylesheetName) {

        try (InputStream stylesheet = getClass().getResourceAsStream(stylesheetName)) {
            XSLTransformer transformer = new XSLTransformer(stylesheet);
            return (Element) transformer.transform(asList((Content) source)).get(0);
        } catch (IOException | XSLTransformException e) {
            throw new RuntimeException(e);
        }

        /*   try (InputStream stylesheet = getClass().getResourceAsStream(stylesheet)) {
         Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet));
         JDOMResult result = new JDOMResult();
         transformer.transform(new JDOMSource(source), result);
         return (Element) result.getResult().get(0);
         } catch (IOException | TransformerException e) {
         throw new RuntimeException(e);
         }*/
    }

    /*
     public String render(Content domTree) {
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
     }*/
    public String render(Content xml) {
        try {
            XMLOutputter out = new XMLOutputter();
            StringWriter writer = new StringWriter();
            out.output(asList(xml), writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String render(Document xml) {
        try {
            XMLOutputter out = new XMLOutputter();
            StringWriter writer = new StringWriter();
            out.output(xml, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> executeXpath(Object root, String query) {
        return xpath.compile(query).evaluate(root);
    }

    public Element executeXpathSingleElement(Object root, String query) {
        List<Object> selected = executeXpath(root, query);
        if (selected.size() != 1) {
            throw new IllegalArgumentException("Exactly one element must match the query " + query
                    + " to extract the one element, got " + selected);
        }
        if (!(selected.get(0) instanceof Element)) {
            throw new IllegalArgumentException("Selected content is not Element: " + selected.get(0));
        }
        return (Element) selected.get(0);
    }

    public String extractAttribute(Element element, String name) {
        Attribute attribute = element.getAttribute(name);
        if (attribute == null) {
            throw new IllegalArgumentException("Element " + element + " doesnt have attribute " + name);
        }
        return attribute.getValue();
    }

    public Document parseFile(String name) {
        return parseStream(getClass().getResourceAsStream(name));
    }

    public Document parseXmlToDocument(String xml) {
        return parseStream(new ByteArrayInputStream(xml.getBytes()));
    }

    public Document parseStream(InputStream xml) {
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
