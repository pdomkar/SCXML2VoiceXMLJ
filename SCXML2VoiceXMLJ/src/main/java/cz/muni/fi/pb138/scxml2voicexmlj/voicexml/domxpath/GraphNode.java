/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphNode {

    private String name;
    private List<GraphNode> pathsTo = new ArrayList<>();

    public GraphNode(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void addPathTo(GraphNode node) {
        pathsTo.add(node);
    }

    public boolean hasPathTo(GraphNode other) {
        return pathsTo.contains(other);
    }

    List<GraphNode> allPaths() {
        return Collections.unmodifiableList(pathsTo);
    }

}
