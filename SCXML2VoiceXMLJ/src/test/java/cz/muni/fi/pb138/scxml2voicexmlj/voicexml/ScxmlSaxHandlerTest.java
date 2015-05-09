/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.xml.sax.SAXException;

public class ScxmlSaxHandlerTest {

    private InputStream input;

    @Before
    public void setup() {
        input = ScxmlSaxHandlerTest.class.getResourceAsStream("/AccountType.scxml");
        System.out.println(input);
    }

    @Test
    public void testStateIsFound() throws SAXException, IOException, ParserConfigurationException {
        ScxmlListener listener = mock(ScxmlListener.class);
        ScxmlSaxHandler handler = new ScxmlSaxHandler(listener);
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(input, handler);

        verify(listener, times(1)).openState();
        verify(listener, times(1)).closeState();
    }

}
