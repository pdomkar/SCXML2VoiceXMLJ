/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import static java.util.Arrays.asList;
import java.util.Collections;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        Document parent = helper.createDocument();
        Element state = helper.parseXmlToElement(
                "<state id='A'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>");
        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, Collections.EMPTY_LIST, parent);
        assertFalse(cond.isAvailable());
    }

    @Test
    public void testOnlyForwardReferences() {
        Document parent = helper.createDocument();
        Element state = helper.parseXmlToElement(
                "<state id='A'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>");
        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("C", "D"), parent);
        assertFalse(cond.isAvailable());
    }

    @Test
    public void testBackwardTransitionsConvertedToClear() {
        Document ignored = helper.createDocument();
        Element state = helper.parseXmlToElement(
                "<state id='C'>"
                + "    <transition event='ev' target='B'/>"
                + "</state>");
        Element vxml = helper.parseXmlToElement(
                "<if expr=\"C=='ev'\" xmlns='http://www.w3.or/2001/vxml'>"
                + "    <clear namelist='B C'/>"
                + "</if>");

        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("A", "B", "C"), ignored);
        assertThat(the(cond.result()), isEquivalentTo(the(vxml)));
    }

    @Test
    public void testTwoBackwardTransitions() {
        Document ignored = helper.createDocument();
        Element state = helper.parseXmlToElement(
                "<state id='D'>"
                + "    <transition event='ev1' target='A'/>"
                + "    <transition event='ev2' target='C'/>"
                + "</state>");
        Element vxml = helper.parseXmlToElement(
                "<if expr=\"D=='ev1'\" xmlns='http://www.w3.or/2001/vxml'>"
                + "    <clear namelist='A B C D'/>"
                + "    <elseif expr=\"D=='ev2'\"/>"
                + "    <clear namelist='C D'/>"
                + "</if>");

        AssemblerResult<Element> cond = conv.assembleClearsForBackwardTransitions(state, asList("A", "B", "C", "D"), ignored);
        assertThat(the(cond.result()), isEquivalentTo(the(vxml)));
    }

    @Test
    public void testBothBackwardsTransitionAndOnExit() {
        fail();
    }

    @Test
    public void foo() {
        System.out.println(conv.convert(getClass().getResourceAsStream("/Registration.scxml"), null));
    }

}
