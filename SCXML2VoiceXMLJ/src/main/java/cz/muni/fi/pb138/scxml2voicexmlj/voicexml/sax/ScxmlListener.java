/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.sax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intended for single use.
 * Do not reuse to parse another file, create a new instance instead.
 */
public class ScxmlListener {

    private List<StateModel> statesFound = new ArrayList<>();
    private StateModel currentState;

    public void openState() {
        currentState = new StateModel();
    }

    public void closeState() {
        statesFound.add(currentState);
        currentState = null;
    }

    public Collection<StateModel> statesFound() {
        return statesFound;
    }

}
