/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.srgs;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author Petr Mejzlik
 */
public interface Srgs {
    /**
     * @param scxmlContent           XML containing the dialog in the SCXML format.
     * 
     * @param grxmlFileNamePrefix    The common prefix of the names of the files 
     *                               where inline grammars from the SCXML file will 
     *                               be stored. Each grammar is stored in its own file. 
     *                               NOTE: The .grxml extension will be appended automatically, 
     *                                     don't use it in the prefix.
     * 
     * @return                       For each state id, the XML code 
     *                               to reference the SRGS grammar of that state
     *                               in VoiceXML.
     */
    Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileNamePrefix);
}
