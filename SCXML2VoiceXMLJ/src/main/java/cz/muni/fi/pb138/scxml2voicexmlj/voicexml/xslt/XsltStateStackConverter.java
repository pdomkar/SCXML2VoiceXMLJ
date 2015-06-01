/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XsltStateStackConverter implements ScxmlToVoicexmlConverter {

    private XmlHelper helper = new XmlHelper();

    private Stack<String> visitedStates = new Stack<>();

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Document emptyVxmlDocument() {
        return helper.parseFile("/vxmlTemplate.xml");
    }

    /* private String convertState(Element scxmlState) {
     String name = scxmlState.getAttribute("id");
     if (visitedStates.contains(this)) {
     visitedStates.add(name);
     }
     Stack<?> s;
     s.
     }*/
    List<Element> transformStates(List<Element> states, Document vxmlParent) {
        System.out.println(states);
        List<Element> transformed = new ArrayList<>();
        for (Element state : states) {
            transformed.add(transformState(state, vxmlParent));
        }
        return transformed;
    }

    private Element transformState(Element state, Document vxmlParent) {
        List<Element> transitions = helper.toElementList(helper.executeXpath(state, "transition"));
        List<Element> clears = new ArrayList<>();
        System.out.println(helper.render(state));
        Element rawField = helper.transformElement(state, "/stateTransformation.xsl");
        System.out.println(rawField);
        Element field = helper.adoptElement(rawField, vxmlParent);
        for (Element tra : transitions) {
            String target = helper.extractAttribute(tra, "target");
            if (visitedStates.contains(target)) {
                int from = visitedStates.indexOf(target);
                int to = visitedStates.size() - 1;
                List<String> fieldsToClear = visitedStates.subList(from, to);
                Element clear = vxmlParent.createElement("clear");
            }
        }
        if (!clears.isEmpty()) {

        }
        return field;
    }

}
