package compiler.CHRIntermediateForm.builder.tables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import util.Resettable;

import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;


/**
 * @author Peter Van Weert
 */
public abstract class SymbolTable<T> implements Iterable<T>, Resettable {
    private Map<String, T> table;
    
    public SymbolTable() {
        setTable(new HashMap<String, T>());
    }
    
    public Map<String, T> getTable() {
        return table;
    }
    
    public Collection<T> getValues() {
        return getTable().values();
    }
    
    public Iterator<T> iterator() {
        return getValues().iterator();
    }
    
    public Set<String> getKeys() {
        return getTable().keySet();
    }
    
    protected void setTable(Map<String, T> table) {
        this.table = table;
    }
   
    public T get(String id) {
        return getTable().get(id);
    }
    
    public boolean isDeclaredId(String id) {
        return getTable().containsKey(id);
    }
    
    public boolean contains(Object t) {
        return getTable().containsValue(t);
    }
    
    protected T declare(String id, T t) throws DuplicateIdentifierException {
        if (id == null) id = createID();
        if (getTable().put(id, t) != null)
            throw new DuplicateIdentifierException(id);
        return t;
    }
    
    protected T declareSafe(String id, T t) {
        if (id == null) id = createID();
        getTable().put(id, t);        
        return t;
    }
    
    protected T ensureDeclared(String id, T t) {
        if (isDeclaredId(id)) 
            return get(id);
        else
            return declareSafe(id, t);        
    }
    
    public String createID() {
        return createID("");
    }
    
    protected String createID(String prefix) {
        return "$" + prefix + "_" + getSize();
    }
    
    public int getSize() {
        return getTable().size();
    }
    
    protected int getCurrentNbr() {
        return getSize();
    }
    protected int getNextNbr() {
        return getCurrentNbr() + 1;
    }
    
    public void reset() {
        getTable().clear();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(100)
            .append(super.toString())
            .append(" contains ")
            .append(getSize())
            .append(" symbol(s):\n");
        
        for (String id : getTable().keySet()) {
            result.append('\t')
                .append(id)
                .append(" --> ")
                .append(get(id))
                .append('\n');
        }
        
        return result.toString();
    }
}