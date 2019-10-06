package util.comparing;

import java.util.Comparator;

public class ReversedComparator<T> implements Comparator<T> {

    private Comparator<T> decorated;
    
    protected ReversedComparator(Comparator<T> decorated) {
        setDecorated(decorated);
    }
    
    public static <T> Comparator<T> reverse(Comparator<T> decorated) {
        if (decorated instanceof ReversedComparator)
            return ((ReversedComparator<T>)decorated).getDecorated();
        else
            return new ReversedComparator<T>(decorated);
    }
    
    public int compare(T o1, T o2) {
        return -getDecorated().compare(o1, o2);
    }
    
    protected Comparator<T> getDecorated() {
        return decorated;
    }
    protected void setDecorated(Comparator<T> decorated) {
        this.decorated = decorated;
    }
}