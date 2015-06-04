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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class XsltStateStackConverter implements ScxmlToVoicexmlConverter {

    static final Namespace NS_SCXML = Namespace.getNamespace("http://www.w3.org/2005/07/scxml");
    static final Namespace NS_VXML = Namespace.getNamespace("http://www.w3.org/2001/vxml");

    private XmlHelper helper = new XmlHelper();

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        Document vxml = emptyVxmlDocument();
        Document scxml = helper.parseStream(scxmlContent);
        Element form = vxml.getRootElement().getChild("form", NS_VXML);// helper.executeXpathSingleElement(vxml, "//*[local-name()='form']");
        appendGrammarFile(form, srgsReferences);
        List<Element> states = scxml.getRootElement().getChildren("state", NS_SCXML);
        String initialName = helper.extractAttribute(scxml.getRootElement(), "initial");
        LinkedHashMap<Element, String> statesWithTransforms = new LinkedHashMap<>();
        for (Element state : states) {
            if (helper.extractAttribute(state, "id").equals(initialName)) {
                statesWithTransforms.put(state, "/initialTransformation.xsl");
            } else {
                statesWithTransforms.put(state, "/stateTransformation.xsl");
            }
        }
        //List<Object> states = helper.executeXpath(scxml, "//*[local-name()='state']");
        for (Element field : transformStates(statesWithTransforms, srgsReferences)) {
            form.addContent(field);
        }
        return helper.render(vxml);
    }

    public void appendGrammarFile(Element form, GrammarReference srgsReferences) {
        Element grammar = new Element("grammar", NS_VXML);
        grammar.setAttribute("src", srgsReferences.grammarFile());
        form.addContent(grammar);
    }

    public void appendGrammarField(Element field, GrammarReference srgsReferences) {
        String name = helper.extractAttribute(field, "name");
        if (!srgsReferences.stateHasGrammarReference(name)) {
            return;
        }
        String reference = srgsReferences.referenceForState(name);
        Element grammar = new Element("grammar", NS_VXML);
        grammar.setAttribute("src", reference);
        field.addContent(grammar);
    }

    public Document emptyVxmlDocument() {
        return helper.parseFile("/vxmlTemplate.xml");
    }

    /** Ordering has to be enforced */
    public List<Element> transformStates(LinkedHashMap<Element, String> statesWithTransforms, GrammarReference grammarReference) {
        List<Element> transformed = new ArrayList<>();
        Stack<String> visitedStates = new Stack<>();
        for (Entry<Element, String> stateTransformPair : statesWithTransforms.entrySet()) {
            Element state = stateTransformPair.getKey();
            String transform = stateTransformPair.getValue();
            visitedStates.add(helper.extractAttribute(state, "id"));
            Element field = helper.transformElement(state, transform);
            AssemblerResult<Element> transitionsAssembler = assembleClearsForBackwardTransitions(state, visitedStates);
            if (transitionsAssembler.isAvailable()) {
                System.out.println("hit");
                Element filled = findFilledElementAppendLazyly(field);
                filled.addContent(transitionsAssembler.result());
            }
            appendGrammarField(field, grammarReference);
            transformed.add(field);
        }
        return transformed;
    }

    private Element findFilledElementAppendLazyly(Element field) {
        if (field.getChild("filled", NS_VXML) == null) {
            Element filled = new Element("filled", NS_VXML);
            field.addContent(filled);
        }
        return field.getChild("filled", NS_VXML);
    }

    public AssemblerResult<Element> assembleClearsForBackwardTransitions(Element state, List<String> visitedStates) {
        System.out.println(state);
        System.out.println(visitedStates);
        String stateName = helper.extractAttribute(state, "id");
        List<Element> transitions = state.getChildren("transition", NS_SCXML);// helper.toElementList(helper.executeXpath(state, "*[local-name()='transition']"));
        ConditionalTransitionsAssembler clearBuilder = new ConditionalTransitionsAssembler();
        System.out.println(transitions);
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
