/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import static cz.muni.fi.pb138.scxml2voicexmlj.voicexml.XsltStateStackConverter.NS_SCXML;
import static cz.muni.fi.pb138.scxml2voicexmlj.voicexml.XsltStateStackConverter.NS_VXML;
import static cz.muni.fi.pb138.scxml2voicexmlj.voicexml.XsltStateStackConverter.TRANSFORM_INITIAL;
import static cz.muni.fi.pb138.scxml2voicexmlj.voicexml.XsltStateStackConverter.TRANSFORM_STATE;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Map;
import org.jdom2.Element;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public class XsltStateStackConverterTest {

    private XmlHelper helper;
    private XsltStateStackConverter conv;

    @Before
    public void setup() {
        helper = new XmlHelper();
        conv = new XsltStateStackConverter();
    }

    @Test
    public void testNoStatesVisited() {
        Element state = helper.parseXmlToDocument(
                "<state id='A'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>").getRootElement();
        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, Collections.EMPTY_LIST);
        assertFalse(cond.isAvailable());
    }

    @Test
    public void testOnlyForwardReferences() {
        Element state = helper.parseXmlToDocument(
                "<state id='A'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>").getRootElement();
        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("C", "D"));
        assertFalse(cond.isAvailable());
    }

    @Test
    public void testBackwardTransitionsConvertedToClear() {
        Element state = helper.parseXmlToDocument(
                "<state id='C' xmlns='http://www.w3.org/2005/07/scxml'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>").getRootElement();
        Element vxml = helper.parseXmlToDocument(
                "<if expr=\"C=='ev'\" xmlns='http://www.w3.org/2001/vxml'>"
                + "    <clear namelist='B C'/>"
                + "</if>").getRootElement();

        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("A", "B", "C"));
        String result = helper.render(cond.result());
        String expected = helper.render(vxml);
        assertThat(the(result), isEquivalentTo(the(expected)));
    }

    @Test
    public void testTwoBackwardTransitions() {
        Element state = helper.parseXmlToDocument(
                "<state id='D' xmlns='http://www.w3.org/2005/07/scxml'>"
                + "    <transition event='ev1' target='A'/>"
                + "    <transition event='ev2' target='C'/>"
                + "</state>").getRootElement();
        Element vxml = helper.parseXmlToDocument(
                "<if expr=\"D=='ev1'\" xmlns='http://www.w3.org/2001/vxml'>"
                + "    <clear namelist='A B C D'/>"
                + "    <elseif expr=\"D=='ev2'\"/>"
                + "    <clear namelist='C D'/>"
                + "</if>").getRootElement();

        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("A", "B", "C", "D"));
        String result = helper.render(cond.result());
        String expected = helper.render(vxml);
        assertThat(the(result), isEquivalentTo(the(expected)));
    }

    @Test
    public void testAppendGrammar() {
        Element field = new Element("field", NS_VXML);
        field.setAttribute("name", "A");
        GrammarReference grammar = mock(GrammarReference.class);
        when(grammar.stateHasGrammarReference("A")).thenReturn(true);
        when(grammar.referenceForState("A")).thenReturn("ref");
        conv.appendGrammarField(field, grammar);
        assertEquals("ref", field.getChild("grammar", NS_VXML).getAttribute("src").getValue());
    }

    @Test
    public void testMapTransformations() {
        Element initial = new Element("state", NS_SCXML);
        initial.setAttribute("id", "A");
        Element normal = new Element("state", NS_SCXML);
        normal.setAttribute("id", "B");
        Map<Element, String> mappedTransforms = conv.assignTransformationsToStates(
                asList(initial, normal), "A");
        assertEquals(TRANSFORM_INITIAL, mappedTransforms.get(initial));
        assertEquals(TRANSFORM_STATE, mappedTransforms.get(normal));
    }

    @Test
    public void foo() {
        GrammarReference gram = mock(GrammarReference.class);
        when(gram.grammarFile()).thenReturn("file");
        when(gram.stateHasGrammarReference(anyString())).thenReturn(true);
        when(gram.referenceForState(anyString())).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0].toString();
            }
        });
        System.out.println(conv.convert(getClass().getResourceAsStream("/Registration.scxml"), gram));
    }

}
