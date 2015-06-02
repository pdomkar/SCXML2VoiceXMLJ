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
     * @param scxmlContent      xml containing the dialog in the scxml format
     * 
     * @param grxmlFileName     path to the grxml file where all SRGS grammars 
     *                          contained directly in {@code scxmlContent} 
     *                          (not in external grxml files) will be put
     * 
     * @return                  for each state id, the XML element code 
     *                          to reference the SRGS grammar of that state
     */
    Map<String,String> getSrgsReferences(InputStream scxmlContent, String grxmlFileName);
}
