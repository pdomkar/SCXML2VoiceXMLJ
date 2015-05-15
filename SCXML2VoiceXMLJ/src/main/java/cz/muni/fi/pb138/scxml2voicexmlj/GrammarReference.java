/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj;

public interface GrammarReference {

    /** Path to the file containing all grammar references for the given file */
    public String grammarFile();

    /** @return true if grammar reference for the state called {@code stateName} is known, false otherwise */
    public boolean stateHasGrammarReference(String stateName);

    /**
     * @return String which will be used verbatim as reference inside the state called {@code stateName}
     * @throws IllegalArgumentException if reference for this state is not known
     */
    public String referenceForState(String stateName);

}
