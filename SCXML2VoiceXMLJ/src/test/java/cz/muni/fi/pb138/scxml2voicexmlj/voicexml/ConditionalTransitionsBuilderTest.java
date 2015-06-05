/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ConditionalTransitionsAssembler;
import cz.muni.fi.pb138.scxml2voicexmlj.XmlHelper;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
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
        Document doc = helper.createDocument();
        ConditionalTransitionsAssembler builder = new ConditionalTransitionsAssembler(doc);
        builder.appendCondition("field", "event", asList("a", "b"));
        assertThat(the(builder.result()), isEquivalentTo(the(
                "<if expr=\"field=='event'\">"
                + "    <clear namelist=\"a b\"/>"
                + "</if>"
        )));
    }

    @Test
    public void testTwoConditions() {
        Document doc = helper.createDocument();
        ConditionalTransitionsAssembler builder = new ConditionalTransitionsAssembler(doc);
        builder.appendCondition("field1", "event1", asList("a", "b"));
        builder.appendCondition("field2", "event2", asList("c", "d", "e"));
        assertThat(the(builder.result()), isEquivalentTo(the(
                "<if expr=\"field1=='event1'\">"
                + "    <clear namelist=\"a b\"/>"
                + "    <elseif expr=\"field2=='event2'\"/>"
                + "    <clear namelist=\"c d e\"/>"
                + "</if>"
        )));
    }

}
