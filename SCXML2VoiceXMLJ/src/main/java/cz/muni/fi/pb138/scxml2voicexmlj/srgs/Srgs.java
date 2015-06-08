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
     * Extracts references to SRGS grammars from SCXML input. The method takes the SCXML input 
     * and for each <state> in it, it generates a string that is a reference to the grammar 
     * that belongs to that state. That string can then be used in VoiceXML (as the content 
     * of the `src` attribute of a `<grammar>` element) to refer to the grammar of the state.
     * Inline grammars (if there are any in the SCXML input) are stored in external grxml files 
     * and then referenced in them.
     * 
     * @param scxmlContent           The SCXML input: XML containing the dialog in the SCXML format.
     * 
     * @param grxmlFileNamePrefix    The common prefix of the names of the files 
     *                               where inline grammars from the SCXML file will 
     *                               be stored. Each grammar is stored in its own file. 
     *                               NOTE: The .grxml extension will be appended automatically, 
     *                                     don't use it in the prefix.
     * 
     * @return                       A map where state IDs are keys and references to grxml files are values. 
     *                               The `null` key is used to store a reference to the grammar of the scxml file from 
     *                               the `<datamodel>` element that is a direct child of the root element `<scxml>`.
     */
    Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileNamePrefix);
}
