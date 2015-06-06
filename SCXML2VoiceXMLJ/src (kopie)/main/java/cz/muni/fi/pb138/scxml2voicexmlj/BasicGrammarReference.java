package cz.muni.fi.pb138.scxml2voicexmlj;

import java.util.Map;

/**
 * Created by Wallecnik on 02.06.15.
 */
public class BasicGrammarReference implements GrammarReference {

    private Map<String, String> references;

    public BasicGrammarReference(Map<String, String> references) {
        this.references = references;
    }

    /**
     * Path to the file containing all grammar references for the given file
     */
    @Override
    public String grammarFile() {
        if (references.containsKey(null)) {
            return references.get(null);
        }
        return null;
    }

    /**
     * @param stateName
     * @return true if grammar reference for the state called {@code stateName} is known, false otherwise
     */
    @Override
    public boolean stateHasGrammarReference(String stateName) {
        return references.containsKey(stateName);
    }

    /**
     * @param stateName
     * @return String which will be used verbatim as reference inside the state called {@code stateName}
     * @throws IllegalArgumentException if reference for this state is not known
     */
    @Override
    public String referenceForState(String stateName) {
        if (!stateHasGrammarReference(stateName)) throw new IllegalArgumentException("Key " + stateName + " is not present");
        return references.get(stateName);
    }
}
