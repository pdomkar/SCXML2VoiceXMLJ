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
        //List<Object> states = helper.executeXpath(scxml, "//*[local-name()='state']");
        for (Element field : transformStates(states, vxml, srgsReferences)) {
            form.addContent(field);
        }
        /*String initialName = helper.extractAttribute(helper.executeXpathSingleElement(scxml, "/*"), "initial");
         Element initial = helper.executeXpathSingleElement(form, "//*[local-name()='field' and @name='" + initialName + "']");
         appendInitialAssign(initial, initialName);*/
        return helper.render(vxml);
    }

    public void appendGrammarFile(Element form, GrammarReference srgsReferences) {
        Element grammar = new Element("grammar", NS_VXML);
        grammar.setAttribute("src", srgsReferences.grammarFile());
        form.addContent(grammar);
        /*
         Element grammar = form.getOwnerDocument().createElementNS("http://www.w3.or/2001/vxml", "grammar");
         grammar.setAttribute("src", srgsReferences.grammarFile());
         form.appendChild(grammar);*/
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

    private void appendInitialAssign(Element initial, String name) {
        Element filled = findFilledElementAppendLazyly(initial);
        Element assign = initial.getOwnerDocument().createElement("assign");
        assign.setAttribute("name", name);
        assign.setAttribute("expr", "true");
        filled.appendChild(assign);
    }

    public List<Element> transformStates(List<Element> states, Document vxmlParent, GrammarReference grammarReference) {
        List<Element> transformed = new ArrayList<>();
        Stack<String> visitedStates = new Stack<>();
        for (Element state : states) {
            visitedStates.add(helper.extractAttribute(state, "id"));
            Element field = helper.transformElement(state, "/stateTransformation.xsl");
            AssemblerResult<Element> transitionsAssembler = assembleClearsForBackwardTransitions(state, visitedStates, vxmlParent);
            if (transitionsAssembler.isAvailable()) {
                Element filled = findFilledElementAppendLazyly(field);
                filled.addContent(transitionsAssembler.result());
            }
            //    appendGrammarField(field, grammarReference);
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

    public AssemblerResult<Element> assembleClearsForBackwardTransitions(Element state, List<String> visitedStates, Document vxmlParent) {
        String stateName = helper.extractAttribute(state, "id");
        List<Element> transitions = state.getChildren("transition");// helper.toElementList(helper.executeXpath(state, "*[local-name()='transition']"));
        ConditionalTransitionsAssembler clearBuilder = new ConditionalTransitionsAssembler();
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
