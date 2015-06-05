/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

import static cz.muni.fi.pb138.scxml2voicexmlj.voicexml.XsltStateStackConverter.NS_VXML;
import java.util.List;
import org.jdom2.Element;

class ConditionalTransitionsAssembler implements AssemblerResult<Element> {

    private Element ifRoot;

    @Override
    public Element result() {
        if (!isAvailable()) {
            throw new IllegalStateException("No conditions set");
        }
        return ifRoot;
    }

    @Override
    public boolean isAvailable() {
        return ifRoot != null;
    }

    /**
     * Add the initial {@code if} if there are no conditions yet, or {@code elseif} otherwise.
     * The product is following
     * <pre> &lt;if expr="NAME=='EVENT'"&gt;
     *    &lt;clear namelist="TARGET1 TARGET2.." /&gt;
     * &lt;/if&gt;</pre>
     * with additional elseifs and clears between the clear and closing if
     * @param name    of the event
     * @param event   which triggers the transition
     * @param targets to be cleared
     */
    public void appendCondition(String name, String event, List<String> targets) {
        if (ifRoot == null) {
            ifRoot = new Element("if", NS_VXML);
            appendExpr(ifRoot, name, event);
            ifRoot.addContent(createClear(targets));
            return;
        }
        Element cond = new Element("elseif", NS_VXML);
        appendExpr(cond, name, event);
        ifRoot.addContent(cond);
        ifRoot.addContent(createClear(targets));
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
        Element clear = new Element("clear", NS_VXML);// parent.createElementNS("http://www.w3.or/2001/vxml", "clear");
        clear.setAttribute("namelist", joinedTargets.toString());
        return clear;
    }

}
