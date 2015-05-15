/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.domxpath;

import java.util.ArrayList;
import java.util.List;

public class GraphHelper {

    public static List<GraphNode> orderedTopologically(List<GraphNode> nodes) {
        List<GraphNode> nodesLeft = new ArrayList<>(nodes);
        List<GraphNode> nodesOrdered = new ArrayList<>(nodes.size());
        while (!nodesLeft.isEmpty()) {
            GraphNode nodeWithoutInbound = selectWithoutInboundPaths(nodesLeft);
            nodesLeft.remove(nodeWithoutInbound);
            nodesOrdered.add(nodeWithoutInbound);
        }
        return nodesOrdered;
    }

    private static GraphNode selectWithoutInboundPaths(List<GraphNode> from) {
        for (GraphNode node : from) {
            if (!inboundPathExistsFor(node, from)) {
                return node;
            }
        }
        throw new IllegalArgumentException("Graph contains cycle");
    }

    private static boolean inboundPathExistsFor(GraphNode node, List<GraphNode> from) {
        for (GraphNode other : from) {
            if (other != node && other.hasPathTo(node)) {
                return true;
            }
        }
        return false;
    }

}
