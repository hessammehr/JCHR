package runtime;

import java.util.Comparator;

public abstract class ConstraintComparator implements Comparator<Constraint> {
    
    ConstraintComparator() { /* FACTORY METHODS */ }
    
    private static Comparator<Constraint> oldFirstInstance;
    public static Comparator<Constraint> getOldFirstComparator() {
        if (oldFirstInstance == null)
            oldFirstInstance = new ConstraintComparator() {
                public int compare(Constraint one, Constraint other) {
                    if (one == other) return 0;
                    return one.isOlderThan(other)? -1 : +1;
                }
            };
        return oldFirstInstance;
    }
    
    private static Comparator<Constraint> newFirstInstance;
    public static Comparator<Constraint> getNewFirstComparator() {
        if (newFirstInstance == null)
            newFirstInstance = new ConstraintComparator() {
                public int compare(Constraint one, Constraint other) {
                    if (one == other) return 0;
                    return one.isNewerThan(other)? -1 : +1;
                }
            };
        return newFirstInstance;
    }

    public static Comparator<Constraint> getDefaultComparator() {
        return getOldFirstComparator();
    }
}
