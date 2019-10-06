package compiler.codeGeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;

import util.Resettable;

/**
 * A utility class used by the code generator to keep track of which comparisons
 * are to be made between partner constraints.
 * 
 * @author Peter Van Weert
 */
public class PartnerConstraintsStore implements Resettable {
    private Map<String, List<String>> store;
    
    private boolean saving;
    private Map<String, List<String>> saved;
    
    public PartnerConstraintsStore(Occurrence occurrence) {
    	this();
    	reset(occurrence.getIdentifier());
    }
    
    public PartnerConstraintsStore() {
        setStore(new HashMap<String, List<String>>());
        setSaved(new HashMap<String, List<String>>());
    }
    
    public void add(String constraintId, String occurrenceId) {
        add(constraintId, occurrenceId, getStore());
        if (isSaving()) 
            add(constraintId, occurrenceId, getSaved());
    }
    
    protected void add(String constraintId, String occurrenceId, 
        Map<String, List<String>> store) {
        
        List<String> list = store.get(constraintId);
        if (list == null) 
            store.put(constraintId, list = new ArrayList<String>(2));
        list.add(occurrenceId);
    }
    
    protected void remove(String constraintId, String occurrenceId) throws NoSuchElementException {
        List<String> list = getStore().get(constraintId);
        if (list == null || ! list.remove(occurrenceId))
            throw new NoSuchElementException();
    }
    
    public void reset() throws IllegalStateException {
        if (saving) throw new IllegalStateException();
        for (List<String> list : getStore().values()) list.clear();
    }
    
    public void reset(String constraintId) throws IllegalStateException {
        reset();
        add(constraintId, "this");
    }
    
    public boolean contains(String constraintId, String occurrenceId) {
        final List<String> list = getStore().get(constraintId);
        return ((list != null) && (list.contains(occurrenceId)));
    }
    
    public List<String> getIdentifiers(String constraintId) {
        return getStore().get(constraintId);
    }

    protected Map<String, List<String>> getStore() {
        return store;
    }
    protected void setStore(Map<String, List<String>> store) {
        this.store = store;
    }
    
    protected Map<String, List<String>> getSaved() {
        return saved;
    }
    protected void setSaved(Map<String, List<String>> saved) {
        this.saved = saved;
    }

    public void save() throws IllegalStateException {
        if (saving) throw new IllegalStateException();
        saving = true;
    }
    
    public void restore() throws IllegalStateException {
        if (! saving) throw new IllegalStateException();
        saving = false;
        
        for (Entry<String, List<String>> entry : saved.entrySet()) {
            String constraintId = entry.getKey();
            List<String> occurrenceIds = entry.getValue();
            for (String occurrenceId : occurrenceIds)
                remove(constraintId, occurrenceId);
            occurrenceIds.clear();
        }
    }

    public boolean isSaving() {
        return saving;
    }
}