/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

class XsltStateStackConverter implements ScxmlToVoicexmlConverter {

    static final Namespace NS_SCXML = Namespace.getNamespace("http://www.w3.org/2005/07/scxml");
    static final Namespace NS_VXML = Namespace.getNamespace("http://www.w3.org/2001/vxml");

    static final String TRANSFORM_INITIAL = "/initialTransformation.xsl";
    static final String TRANSFORM_STATE = "/stateTransformation.xsl";

    private XmlHelper helper = new XmlHelper();

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        Document vxml = emptyVxmlDocument();
        Document scxml = helper.parseStream(scxmlContent);
        Element form = vxml.getRootElement().getChild("form", NS_VXML);
        appendGrammarFile(form, srgsReferences);
        List<Element> states = new ArrayList<>(scxml.getRootElement().getChildren("state", NS_SCXML));
        states.addAll(scxml.getRootElement().getChildren("final", NS_SCXML));
        String initialName = helper.extractAttribute(scxml.getRootElement(), "initial");
        List<Element> transformedVxmlStates = transformStates(assignTransformationsToStates(states, initialName), srgsReferences);
        form.addContent(transformedVxmlStates);
        return helper.render(vxml);
    }

    /**
     * For every state select transformation which should be used on it.
     * transformations are values associated with the state elements as keys
     */
    public LinkedHashMap<Element, String> assignTransformationsToStates(List<Element> states, String initialState) {
        LinkedHashMap<Element, String> statesWithTransforms = new LinkedHashMap<>();
        for (Element state : states) {
            if (helper.extractAttribute(state, "id").equals(initialState)) {
                statesWithTransforms.put(state, TRANSFORM_INITIAL);
            } else {
                statesWithTransforms.put(state, TRANSFORM_STATE);
            }
        }
        return statesWithTransforms;
    }

    /**
     * Append element pointing to file with srgs grammar to the form
     */
    public void appendGrammarFile(Element form, GrammarReference srgsReferences) {
        Element grammar = new Element("grammar", NS_VXML);
        grammar.setAttribute("src", srgsReferences.grammarFile());
        form.addContent(grammar);
    }

    /**
     * Append element pointing to reference inside the grammar file to the field
     */
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

    /**
     * Performs transformations on every state and adds grammar reference.
     * Remembers which states were already visited so that clear elements for backward navigation can be created.
     */
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
                Element filled = getFilledElementAppendLazyly(field);
                filled.addContent(transitionsAssembler.result());
            }
            appendGrammarField(field, grammarReference);
            transformed.add(field);
        }
        return transformed;
    }

    /**
     * Return the {@code <filled>} element of {@code field}.
     * If it didnt exists, create it first
     */
    public Element getFilledElementAppendLazyly(Element field) {
        if (field.getChild("filled", NS_VXML) == null) {
            Element filled = new Element("filled", NS_VXML);
            field.addContent(filled);
        }
        return field.getChild("filled", NS_VXML);
    }

    /**
     * Assemble the structure containing conditional logic for navigating backwards in the dialogue.
     * Ignore every transition pointing forward and for every transition pointing to an already visited state A
     * create a {code clear} element with fields from the most recent state up to A in appropriate if/elseif struct.
     * <p>
     * E.g. if we are in C, have already visited A and B and C has two transitions, one pointing to D
     * (event d) and the other to B (event b), the result of this method will be following
     * <pre> &lt;if expr="C=='b'"&gt;
     *    &lt;clear namelist="B C" /&gt;
     * &lt;/if&gt;</pre>
     * If there are no backward transitions, there will be no element to obtain from the result and
     * its {@code isAvailable()} method returns false
     */
    public AssemblerResult<Element> assembleClearsForBackwardTransitions(Element state, List<String> visitedStates) {
        String stateName = helper.extractAttribute(state, "id");
        List<Element> transitions = state.getChildren("transition", NS_SCXML);
        ConditionalTransitionsAssembler clearBuilder = new ConditionalTransitionsAssembler();
        for (Element transition : transitions) {
            String target = helper.extractAttribute(transition, "target");
            String event = helper.extractAttribute(transition, "event");
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
