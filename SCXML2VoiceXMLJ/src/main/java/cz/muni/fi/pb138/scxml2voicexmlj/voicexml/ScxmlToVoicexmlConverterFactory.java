/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.ScxmlToVoicexmlConverter;

public class ScxmlToVoicexmlConverterFactory {

    /**
     * Obtain instance of {@code ScxmlToVoicexmlConverter}
     */
    public ScxmlToVoicexmlConverter createConverter() {
        return new SaxBasedScxmlToVoicexmlConverter();
    }

}
