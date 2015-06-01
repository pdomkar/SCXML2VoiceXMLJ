/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConditionalTransitionsBuilder {

    private Document parent;
    private Element ifRoot;

    public ConditionalTransitionsBuilder(Document parent) {
        this.parent = parent;
    }

    public Element build() {
        if (ifRoot == null) {
            throw new RuntimeException("No conditions set");
        }
        return ifRoot;
    }

    public boolean hasAny() {
        return ifRoot != null;
    }

    public void appendCondition(String name, String event, List<String> targets) {
        if (ifRoot == null) {
            ifRoot = parent.createElementNS("http://www.w3.or/2001/vxml", "if");
            appendExpr(ifRoot, name, event);
            ifRoot.appendChild(createClear(targets));
            return;
        }
        Element cond = parent.createElementNS("http://www.w3.or/2001/vxml", "elseif");
        appendExpr(cond, name, event);
        ifRoot.appendChild(cond);
        ifRoot.appendChild(createClear(targets));
    }

    private void appendExpr(Element cond, String name, String event) {
        cond.setAttribute("expr", name + "=='" + event + "'");
    }

    private Element createClear(List<String> targets) {
        StringBuilder joinedTargets = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {
            if (i != 0) {
                joinedTargets.append(" ");
            }
            joinedTargets.append(targets.get(i));
        }
        Element clear = parent.createElementNS("http://www.w3.or/2001/vxml", "clear");
        clear.setAttribute("namelist", joinedTargets.toString());
        return clear;
    }

}
