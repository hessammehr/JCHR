package util.comparing;

import java.util.Comparator;

public class ClassComparator implements Comparator<Class<?>> {
	private ClassComparator() { /* SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static ClassComparator instance;
    @SuppressWarnings("unchecked")
    public static Comparator<Class<?>> getInstance() {
        if (instance == null)
            instance = new ClassComparator();
        return instance;
    }
    
    public int compare(Class<?> o1, Class<?> o2) {
    	if (o1 == o2) return 0;
    	if (o1 == null) return -1;
    	if (o2 == null) return +1;
    	return o1.getName().compareTo(o2.getName());
    }
}
