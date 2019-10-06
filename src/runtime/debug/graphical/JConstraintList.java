package runtime.debug.graphical;

import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import runtime.Constraint;

public class JConstraintList extends JList {
    private static final long serialVersionUID = 1L;

    public final static Color 
        REACTIVIATED_SELECTION_COLOR = Color.CYAN,
        STORED_SELECTION_COLOR = Color.GREEN,
        REMOVED_SELECTION_COLOR = Color.RED;

    
    public JConstraintList() {
        this(new ConstraintListModel());
    }
    
    public JConstraintList(ConstraintListModel model) {
        super(model);
        setSelectionModel(new ConstraintListSelectionModel());
    }

    public void deactivate() {
        invokeLater(new Runnable() {
            public void run() {
                getConstraintSelectionModel().deselect();
            }
        });
    }
    
    public void stored(final Constraint constraint) {
        invokeLater(new Runnable() {
            public void run() {
                int index = getModel().insert(constraint);
                doSelect(index, STORED_SELECTION_COLOR);
            }
        });
    }
    
    public void doRemove(final Constraint constraint) {
        invokeLater(new Runnable() {
            public void run() {
                getModel().remove(constraint);
            }
        });
    }
    
    public void reactivate(final Constraint constraint) {
        invokeLater(new Runnable() {
            public void run() {
                int index = getModel().indexOf(constraint);
                getModel().contentsChanged(index);
                doSelect(index, REACTIVIATED_SELECTION_COLOR);
            }
        });
    }
    
    public void refresh() {
        invokeLater(new Runnable() {
            public void run() {
                getModel().refresh();
            }
        });
    }
    
    public void removed(final Constraint constraint) {
        invokeLater(new Runnable() {
            public void run() {
                doSelect(constraint, REMOVED_SELECTION_COLOR);
            }
        });
    }
    
    public void reverse() {
        invokeLater(new Runnable() {
            public void run() {
                getModel().reverse();
                if (!getConstraintSelectionModel().isSelectionEmpty())
                    doSelect(getModel().getSize()-1 - getConstraintSelectionModel().getSelectedIndex());
            }
        });
    }
    public void sortBy(final Comparator<Constraint> comparator) {
        invokeLater(new Runnable() {
            public void run() {
                getModel().sortBy(comparator);
            }
        });
    }
    
    protected void doSelect(Constraint constraint, Color color) {
        doSelect(indexOf(constraint), color);
    }
    protected void doSelect(int index) {
        doSelect(index, getSelectionBackground());
    }
    protected void doSelect(int index, Color color) {
        setSelectionBackground(color);
        getConstraintSelectionModel().select(index);
        ensureIndexIsVisible(index);
    }
    protected int indexOf(Constraint constraint) {
        return getModel().indexOf(constraint);
    }
    
    @Override
    public ConstraintListModel getModel() {
        return (ConstraintListModel)super.getModel();
    }
    @Override
    public void setListData(Object[] listData) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setModel(ListModel model) {
        if (! (model instanceof ConstraintListModel))
            throw new IllegalArgumentException();
        super.setModel(model);
    }
    
    public ConstraintListSelectionModel getConstraintSelectionModel() {
        return (ConstraintListSelectionModel)getSelectionModel();
    }
}
