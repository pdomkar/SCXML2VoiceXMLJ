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
        List<Element> transformed = new ArrayList<>();
        for (Element state : states) {
            visitedStates.add(helper.extractAttribute(state, "id"));
            transformed.add(transformState(state, vxmlParent));

        }
        return transformed;
    }

    private Element transformState(Element state, Document vxmlParent) {
        String stateName = helper.extractAttribute(state, "id");
        List<Element> transitions = helper.toElementList(helper.executeXpath(state, "*[local-name()='transition']"));
        ConditionalTransitionsBuilder clearBuilder = new ConditionalTransitionsBuilder(vxmlParent);
        Element rawField = helper.transformElement(state, "/stateTransformation.xsl");
        Element field = helper.adoptElement(rawField, vxmlParent);
        for (Element tra : transitions) {
            String target = helper.extractAttribute(tra, "target");
            String event = helper.extractAttribute(tra, "event");
            System.out.println(event + "->" + target + " already visited " + visitedStates);
            if (visitedStates.contains(target)) {
                int from = visitedStates.indexOf(target);
                int to = visitedStates.size();
                List<String> fieldsToClear = visitedStates.subList(from, to);
                System.out.println(visitedStates);
                System.out.println(fieldsToClear);
                System.out.println(from + "-" + to);
                clearBuilder.appendCondition(stateName, event, fieldsToClear);
            }
        }
        if (clearBuilder.hasAny()) {
            field.appendChild(clearBuilder.build());
        }
        return field;
    }

}
