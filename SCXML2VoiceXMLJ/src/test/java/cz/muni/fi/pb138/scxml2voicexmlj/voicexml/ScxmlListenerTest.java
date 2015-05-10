/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.sax.ScxmlListener;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.sax.StateModel;
import java.util.Collection;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class ScxmlListenerTest {

    @Test
    public void testCollectionOfStatesIsCreated() {
        ScxmlListener listener = new ScxmlListener();
        listener.openState();
        listener.closeState();
        listener.openState();
        listener.closeState();
        Collection<StateModel> statesFound = listener.statesFound();
        assertThat(statesFound).hasSize(2);
    }

}
