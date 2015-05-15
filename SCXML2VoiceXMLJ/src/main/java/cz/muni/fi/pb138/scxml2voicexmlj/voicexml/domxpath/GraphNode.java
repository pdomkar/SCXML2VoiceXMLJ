/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

    private List<GraphNode> pathTo = new ArrayList<>();

    public void addPathTo(GraphNode node) {
        pathTo.add(node);
    }

    public boolean hasPathTo(GraphNode other) {
        return pathTo.contains(other);
    }

    @Override
    public String toString() {
        return "<" + (System.identityHashCode(this) % 100) + ": " + pathTo + ">";
    }

}
