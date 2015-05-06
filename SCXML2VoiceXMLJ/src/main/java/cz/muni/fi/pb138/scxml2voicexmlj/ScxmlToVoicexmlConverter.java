/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

import java.io.InputStream;
import java.util.Map;

public interface ScxmlToVoicexmlConverter {

    /**
     * @param scxmlContent   xml containing the dialog in the scxml format
     * @param srgsReferences map from state names to references inserted into the voicexml output
     * @return content of the {@code scxmlContent} in the voicexml format
     */
    String convert(InputStream scxmlContent, Map<String, String> srgsReferences);

}
