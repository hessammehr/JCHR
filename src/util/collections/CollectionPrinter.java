package util.collections;

import static util.iterator.IteratorUtilities.deepAppendTo;
import static util.iterator.IteratorUtilities.deepToString;

import java.util.Collection;

public class CollectionPrinter {
    private String prefix, postfix, infix;
    
    private static CollectionPrinter javaDefaultInstance;
    public static CollectionPrinter getJavaDefaultInstance() {
        if (javaDefaultInstance == null)
            javaDefaultInstance = new UnmodifiableListPrinter("[", "]", ", ");
        return javaDefaultInstance;
    }
    private static CollectionPrinter commaSeperatedInstance;
    public static CollectionPrinter getCommaSeperatedInstance() {
        if (commaSeperatedInstance == null)
            commaSeperatedInstance = new UnmodifiableListPrinter("", "", ", ");
        return commaSeperatedInstance;
    }
    
    public CollectionPrinter(String prefix, String postfix, String infix) {
        setPrefix(prefix);
        setInfix(infix);
        setPostfix(postfix);
    }
    
    public <E> StringBuilder appendTo(StringBuilder appendable, Collection<E> collection) {
        return deepAppendTo(appendable, collection.iterator(), getPrefix(), getPostfix(), getInfix());
    }
    
    public <E> StringBuffer appendTo(StringBuffer appendable, Collection<E> collection) {
        return deepAppendTo(appendable, collection.iterator(), getPrefix(), getPostfix(), getInfix());
    }
    
    public <E> String toString(Collection<E> collection) {
        return deepToString(collection.iterator(), getPrefix(), getPostfix(), getInfix());
    }
    
    public String getInfix() {
        return infix;
    }
    public void changeInfix(String infix) throws UnsupportedOperationException {
        setInfix(infix);
    }
    protected void setInfix(String infix) {
        this.infix = infix;
    }

    public String getPostfix() {
        return postfix;
    }
    public void changePostfix(String postfix) throws UnsupportedOperationException {
        setPostfix(postfix);
    }
    protected void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getPrefix() {
        return prefix;
    }
    public void changePrefix(String prefix) throws UnsupportedOperationException {
        setPrefix(prefix);
    }
    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    protected static class UnmodifiableListPrinter extends CollectionPrinter {
        public UnmodifiableListPrinter(String prefix, String postfix, String infix) {
            super(prefix, postfix, infix);
        }

        @Override
        public void changeInfix(String infix) {
            throw new UnsupportedOperationException("Unmodifiable printer");
        }
        
        @Override
        public void changePostfix(String postfix) {
            throw new UnsupportedOperationException("Unmodifiable printer");
        }
        
        @Override
        public void changePrefix(String prefix) {
            throw new UnsupportedOperationException("Unmodifiable printer");
        }
    }
}
