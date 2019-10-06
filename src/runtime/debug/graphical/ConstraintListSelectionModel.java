package runtime.debug.graphical;

import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConstraintListSelectionModel implements ListSelectionModel {

    protected int activeIndex;
    
    public void select(int index) {
        fireValueChanged(activeIndex, true);
        activeIndex = index;
        fireValueChanged(index, false);
    }
    public void deselect() {
        int index = activeIndex;
        activeIndex = -1;
        fireValueChanged(index, true);
    }
    
    /**
     * @param isAdjusting true if this is the final change in a
     *  series of adjustments
     * @param index the index that has changed
     * @see EventListenerList
     */
    protected void fireValueChanged(int index, boolean isAdjusting) {
        Object[] listeners = listenerList.getListenerList();
        ListSelectionEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null)
                    e = new ListSelectionEvent(this, index, index, isAdjusting);
                ((ListSelectionListener)listeners[i+1]).valueChanged(e);
            }
        }
    }
    
    protected EventListenerList listenerList = new EventListenerList();
    
    public void addListSelectionListener(ListSelectionListener l) {
        listenerList.add(ListSelectionListener.class, l);
    }
    public void removeListSelectionListener(ListSelectionListener l) {
        listenerList.remove(ListSelectionListener.class, l);
    }

    public void addSelectionInterval(int index0, int index1) {
        // NOP
    }

    public void clearSelection() {
        // NOP
    }

    public int getAnchorSelectionIndex() {
        return -1;
    }

    public int getLeadSelectionIndex() {
        return activeIndex;
    }

    public int getMaxSelectionIndex() {
        return activeIndex;
    }

    public int getMinSelectionIndex() {
        return activeIndex;
    }
    
    public int getSelectedIndex() {
        return activeIndex;
    }

    public int getSelectionMode() {
        return SINGLE_SELECTION;
    }
    
    public boolean getValueIsAdjusting() {
        return false;
    }

    public void insertIndexInterval(int index, int length, boolean before) {
        // NOP
    }

    public boolean isSelectedIndex(int index) {
        return index == activeIndex;
    }

    public boolean isSelectionEmpty() {
        return activeIndex == -1;
    }

    public void removeIndexInterval(int index0, int index1) {
        // NOP
    }

    public void removeSelectionInterval(int index0, int index1) {
        // NOP
    }

    public void setAnchorSelectionIndex(int index) {
        // NOP
    }

    public void setLeadSelectionIndex(int index) {
        // NOP
    }

    public void setSelectionInterval(int index0, int index1) {
        // NOP
    }

    public void setSelectionMode(int selectionMode) {
        // NOP
    }

    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        // NOP
    }
}
