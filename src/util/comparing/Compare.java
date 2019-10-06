package util.comparing;

import java.util.Comparator;

public class Compare {
    @SuppressWarnings("unchecked")
    public static Comparator<?> getComparator(Class<?> clazz) {
        if (clazz.isPrimitive() || java.lang.Comparable.class.isAssignableFrom(clazz))
            return ComparableChecker.getInstance();
        else if (clazz.isArray() && java.lang.Comparable.class.isAssignableFrom(clazz.getComponentType()))
            return LexComparator.getInstance();
        else if (clazz == Class.class)
        	return ClassComparator.getInstance();
        else
            return null;
    }
    
    @SuppressWarnings("unchecked")
    public static int doCompare(Object o1, Object o2) {
        return ((Comparator)getComparator(o1.getClass())).compare(o1, o2);
    }
    
    public static boolean isComparable(Class<?> clazz) {
        return (getComparator(clazz) != null);
    }
}
