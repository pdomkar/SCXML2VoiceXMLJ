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

    private XPathFactory xpath = XPathFactory.instance();

    /**
     * Use provided XSL transform from resources to transform {@code source}
     */
    public Element transformElement(Element source, String stylesheetName) {
        try (InputStream stylesheet = getClass().getResourceAsStream(stylesheetName)) {
            XSLTransformer transformer = new XSLTransformer(stylesheet);
            return (Element) transformer.transform(asList((Content) source)).get(0);
        } catch (IOException | XSLTransformException e) {
            throw new RuntimeException(e);
        }
    }

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
        return render(xml.getRootElement());
    }

    public List<Object> executeXpath(Object root, String query) {
        return xpath.compile(query).evaluate(root);
    }

    /**
     * Execute xpath starting in {@code root}, result is expected to be single element
     * @throws IllegalArgumentException if result is not one element
     */
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

    /**
     * Get attribute value from {@code element}
     * @throws IllegalArgumentException if no such attribute is present
     */
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
