/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class GraphHelperTest {

    @Test
    public void testOrderedTopologically() {
        GraphNode n1 = new GraphNode();
        GraphNode n2 = new GraphNode();
        GraphNode n3 = new GraphNode();
        GraphNode n4 = new GraphNode();
        n1.addPathTo(n2);
        n1.addPathTo(n4);
        n2.addPathTo(n3);
        n2.addPathTo(n4);
        n4.addPathTo(n3);
        assertThat(GraphHelper.orderedTopologically(asList(n1, n2, n3, n4))).containsExactly(n1, n2, n4, n3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrderedTopologicallyWithCycle() {
        GraphNode n1 = new GraphNode();
        GraphNode n2 = new GraphNode();
        GraphNode n3 = new GraphNode();
        n1.addPathTo(n2);
        n2.addPathTo(n3);
        n3.addPathTo(n1);
        GraphHelper.orderedTopologically(asList(n2, n1, n3));
    }

}
