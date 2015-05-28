/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.sax;

import cz.muni.fi.pb138.scxml2voicexmlj.GrammarReference;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class SaxBasedScxmlToVoicexmlConverter implements ScxmlToVoicexmlConverter {

    @Override
    public String convert(InputStream scxmlContent, GrammarReference srgsReferences) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void process(InputStream input, DefaultHandler handler) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(input, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
