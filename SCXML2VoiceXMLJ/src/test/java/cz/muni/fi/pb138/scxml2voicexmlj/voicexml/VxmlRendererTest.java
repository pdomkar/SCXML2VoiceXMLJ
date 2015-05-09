/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import java.util.ArrayList;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class VxmlRendererTest {

    @Test
    public void testOutputContainsVxmlRootTag() {
        VxmlRenderer renderer = new VxmlRenderer();
        String result = renderer.render(new ArrayList<StateModel>());
        assertThat(result).contains("vxml");
    }

}
