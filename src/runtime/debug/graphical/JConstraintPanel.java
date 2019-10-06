package runtime.debug.graphical;

import java.awt.BorderLayout;
import java.lang.reflect.Method;
import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import runtime.Constraint;
import runtime.ConstraintComparator;
import runtime.debug.BasicInterruptingTracer;
import util.comparing.Compare;

public class JConstraintPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private Class<? extends Constraint> constraintClass;
    
    private JConstraintList content;
    
    public JConstraintPanel(BasicInterruptingTracer tracer, Class<? extends Constraint> constraint) {
        super(new BorderLayout());
        setConstraintClass(constraint);
        add(new JConstraintListLabel(tracer, this), BorderLayout.NORTH);
        add(new JScrollPane(content = new JConstraintList()));
    }
    
    public void deactivate() {
        content.deactivate();
    }
    
    public void removed(Constraint constraint) {
        content.removed(constraint);
    }

    public void reactivated(Constraint constraint) {
        content.reactivate(constraint);
    }

    public void doRemove(Constraint constraint) {
        content.doRemove(constraint);
    }

    public void stored(Constraint constraint) {
        content.stored(constraint);
    }

    public void refresh() {
        content.refresh();
    }
    
    void defaultSort() {
        content.sortBy(ConstraintComparator.getDefaultComparator());
    }
    @SuppressWarnings("unchecked")
    void sortOn(String id) {
        final Method getter = getGetter(id);
        final Comparator comparator = Compare.getComparator(getter.getReturnType());
        content.sortBy(new Comparator<Constraint>() {
            public int compare(Constraint o1, Constraint o2) {
                try {
                    return comparator.compare(getter.invoke(o1), getter.invoke(o2));
                } catch (Exception x) {
                	System.out.println(o1);
                	System.out.println(o2);
                	System.out.println(getter);
                    throw new RuntimeException(x);
                }
            }
        });
    }
    void reverse() {
        content.reverse();
    }
    
    public Class<? extends Constraint> getConstraintClass() {
        return constraintClass;
    }
    protected void setConstraintClass(Class<? extends Constraint> constraint) {
        this.constraintClass = constraint;
    }
    
    protected Method getGetter(String id) {
        try {
            return getConstraintClass().getDeclaredMethod("get" + id);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}