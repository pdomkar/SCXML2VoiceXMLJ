/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import static java.util.Arrays.asList;
import org.jdom2.Element;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public class ConditionalTransitionsBuilderTest {

    private XmlHelper helper;

    @Before
    public void setup() {
        helper = new XmlHelper();
    }

    @Test
    public void testSingleCondition() {
        ConditionalTransitionsAssembler builder = new ConditionalTransitionsAssembler();
        builder.appendCondition("field", "event", asList("a", "b"));
        Element result = builder.result();
        assertThat(the(helper.render(result)), isEquivalentTo(the(
                "<if expr=\"field=='event'\" xmlns=\"http://www.w3.org/2001/vxml\">"
                + "    <clear namelist=\"a b\"/>"
                + "</if>"
        )));
    }

    @Test
    public void testTwoConditions() {
        ConditionalTransitionsAssembler builder = new ConditionalTransitionsAssembler();
        builder.appendCondition("field1", "event1", asList("a", "b"));
        builder.appendCondition("field2", "event2", asList("c", "d", "e"));
        Element result = builder.result();
        assertThat(the(helper.render(result)), isEquivalentTo(the(
                "<if expr=\"field1=='event1'\" xmlns=\"http://www.w3.org/2001/vxml\">"
                + "    <clear namelist=\"a b\"/>"
                + "    <elseif expr=\"field2=='event2'\"/>"
                + "    <clear namelist=\"c d e\"/>"
                + "</if>"
        )));
    }

}
