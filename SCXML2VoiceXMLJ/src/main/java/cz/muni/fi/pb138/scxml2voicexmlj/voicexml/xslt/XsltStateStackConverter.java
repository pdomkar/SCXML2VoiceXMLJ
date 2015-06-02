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

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        Document vxml = emptyVxmlDocument();
        Document scxml = helper.parseStream(scxmlContent);
        Element form = helper.executeXpathSingleElement(vxml, "//*[local-name()='form']");
        appendGrammarFile(form, srgsReferences);
        List<Element> states = helper.toElementList(helper.executeXpath(scxml, "//*[local-name()='state']"));
        for (Element field : transformStates(states, vxml, srgsReferences)) {
            form.appendChild(field);
        }
        return helper.render(vxml);
    }

    public void appendGrammarFile(Element form, GrammarReference srgsReferences) {
        Element grammar = form.getOwnerDocument().createElement("grammar");
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
        Element nomatch = helper.executeXpathSingleElement(field, "./*[name()='nomatch']");
        field.insertBefore(grammar, nomatch);
    }

    public Document emptyVxmlDocument() {
        return helper.parseFile("/vxmlTemplate.xml");
    }

    public List<Element> transformStates(List<Element> states, Document vxmlParent, GrammarReference grammarReference) {
        List<Element> transformed = new ArrayList<>();
        Stack<String> visitedStates = new Stack<>();
        for (Element state : states) {
            visitedStates.add(helper.extractAttribute(state, "id"));
            Element field = commonScxmlToVxmlTransform(state, vxmlParent);
            AssemblerResult<Element> transitionsAssembler = assembleClearsForBackwardTransitions(state, visitedStates, vxmlParent);
            if (transitionsAssembler.isAvailable()) {
                Element filled = appendFilledElementLazyly(field);
                filled.appendChild(transitionsAssembler.result());
            }
            appendGrammarField(field, grammarReference);
            transformed.add(field);
        }
        return transformed;
    }

    public Element commonScxmlToVxmlTransform(Element state, Document vxmlParent) {
        Element rawField = helper.transformElement(state, "/stateTransformation.xsl");
        return helper.adoptElement(rawField, vxmlParent);
    }

    private Element appendFilledElementLazyly(Element field) {
        if (helper.executeXpath(field, "*[local-name()='filled']").getLength() == 0) {
            Element filled = field.getOwnerDocument().createElement("filled");
            field.appendChild(filled);
        }
        return helper.executeXpathSingleElement(field, "*[local-name()='filled']");
    }

    public AssemblerResult<Element> assembleClearsForBackwardTransitions(Element state, List<String> visitedStates, Document vxmlParent) {
        String stateName = helper.extractAttribute(state, "id");
        List<Element> transitions = helper.toElementList(helper.executeXpath(state, "*[local-name()='transition']"));
        ConditionalTransitionsAssembler clearBuilder = new ConditionalTransitionsAssembler(vxmlParent);
        for (Element tra : transitions) {
            String target = helper.extractAttribute(tra, "target");
            String event = helper.extractAttribute(tra, "event");
            if (visitedStates.contains(target)) {
                int from = visitedStates.indexOf(target);
                int to = visitedStates.size();
                List<String> fieldsToClear = visitedStates.subList(from, to);
                clearBuilder.appendCondition(stateName, event, fieldsToClear);
            }
        }
        return clearBuilder;
    }

}
