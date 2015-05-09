/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt.XsltBasedScxmlToVoicexmlConverter;
import java.io.InputStream;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class XsltBasedScxmlToVoicexmlConverterTest {

    @Test
    public void testRunSimpleConversion() {
        InputStream stylesheet = getClass().getResourceAsStream("/stylesheetHello.xsl");
        InputStream toBeConverted = getClass().getResourceAsStream("/AccountType.scxml");
        XsltBasedScxmlToVoicexmlConverter conv = new XsltBasedScxmlToVoicexmlConverter(stylesheet);
        String result = conv.convert(toBeConverted, null);
        assertThat(result).contains("Hello");
    }

}
