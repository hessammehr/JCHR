package runtime.debug.graphical;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import runtime.Constraint;
import runtime.debug.BasicInterruptingTracer;
import util.ColorUtils;
import util.comparing.Compare;

public class JConstraintListLabel extends JButton {
    private static final long serialVersionUID = 1L;
    
    private JConstraintPanel panel;
    
    public JConstraintListLabel(
        final BasicInterruptingTracer interruptor, 
        final JConstraintPanel panel
    ) {
        super(Constraint.getIdentifierOf(panel.getConstraintClass()));
        setAlignmentX(SwingConstants.CENTER);
        setPanel(panel);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        final JPopupMenu popup = new JPopupMenu();
        
        //--------------------
        JMenu interruptMenu = new JMenu("Interrupt if ");
        popup.add(interruptMenu);

        final Class<? extends Constraint> clazz = panel.getConstraintClass();
        
        final JCheckBoxMenuItem ifActivated = new JCheckBoxMenuItem();
        ifActivated.setSelected(interruptor.warrantsInterruptIfActivated(clazz));
        ifActivated.setAction(new AbstractAction("Activated") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfActivated(clazz, ifActivated.isSelected());
            }
        });
        interruptMenu.add(ifActivated);
        
        final JCheckBoxMenuItem ifSuspended = new JCheckBoxMenuItem();
        ifSuspended.setSelected(interruptor.warrantsInterruptIfSuspended(clazz));
        ifSuspended.setAction(new AbstractAction("Suspended") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfSuspended(clazz, ifSuspended.isSelected());
            }
        });
        interruptMenu.add(ifSuspended);
        
        final JCheckBoxMenuItem ifReactivated = new JCheckBoxMenuItem();
        ifReactivated.setSelected(interruptor.warrantsInterruptIfReactivated(clazz));
        ifReactivated.setAction(new AbstractAction("Reactivated") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfReactivated(clazz, ifReactivated.isSelected());
            }
        });
        interruptMenu.add(ifReactivated);
        
        final JCheckBoxMenuItem ifStored = new JCheckBoxMenuItem();
        ifStored.setSelected(interruptor.warrantsInterruptIfStored(clazz));
        ifStored.setAction(new AbstractAction("Stored") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfStored(clazz, ifStored.isSelected());
            }
        });
        interruptMenu.add(ifStored);
        
        final JCheckBoxMenuItem ifRemoved = new JCheckBoxMenuItem();
        ifRemoved.setSelected(interruptor.warrantsInterruptIfRemoved(clazz));
        ifRemoved.setAction(new AbstractAction("Removed") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfRemoved(clazz, ifRemoved.isSelected());
            }
        });
        interruptMenu.add(ifRemoved);
        
        final JCheckBoxMenuItem ifTerminated = new JCheckBoxMenuItem();
        ifTerminated.setSelected(interruptor.warrantsInterruptIfTerminated(clazz));
        ifTerminated.setAction(new AbstractAction("Terminated") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                interruptor.interuptIfTerminated(clazz, ifTerminated.isSelected());
            }
        });
        interruptMenu.add(ifTerminated);
        
        interruptMenu.addSeparator();
        
        interruptMenu.add(new JMenuItem(new AbstractAction("All") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                if (!ifActivated.isSelected()) ifActivated.doClick();
                if (!ifReactivated.isSelected()) ifReactivated.doClick();
                if (!ifStored.isSelected()) ifStored.doClick();
                if (!ifRemoved.isSelected()) ifRemoved.doClick();
                if (!ifTerminated.isSelected()) ifTerminated.doClick();
            }
        }));
        
        interruptMenu.add(new JMenuItem(new AbstractAction("Default") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                if (ifActivated.isSelected()) ifActivated.doClick();
                if (!ifReactivated.isSelected()) ifReactivated.doClick();
                if (!ifStored.isSelected()) ifStored.doClick();
                if (!ifRemoved.isSelected()) ifRemoved.doClick();
                if (ifTerminated.isSelected()) ifTerminated.doClick();
            }
        }));
        
        interruptMenu.add(new JMenuItem(new AbstractAction("None") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                if (ifActivated.isSelected()) ifActivated.doClick();
                if (ifReactivated.isSelected()) ifReactivated.doClick();
                if (ifStored.isSelected()) ifStored.doClick();
                if (ifRemoved.isSelected()) ifRemoved.doClick();
                if (ifTerminated.isSelected()) ifTerminated.doClick();
            }
        }));
        
        //--------------------
        JMenu sortMenu = new JMenu("Sort on ");
        popup.add(sortMenu);
        ButtonGroup menuItems = new ButtonGroup();

        sortMenu.add(new PopupMenuItem(menuItems));
        for (String field : Constraint.getFieldNamesOf(panel.getConstraintClass()))
            if (Compare.isComparable(panel.getGetter(field).getReturnType()))
                sortMenu.add(new PopupMenuItem(menuItems, field));
        
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JConstraintListLabel label = JConstraintListLabel.this; 
                popup.show(label, 0, label.getHeight());
            }
        });
    }
    
    protected class PopupMenuItem extends JMenuItem {
        private static final long serialVersionUID = 1L;
        
        protected class PopupMenuItemIcon implements Icon {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                if (timesSelected > 0 && timesSelected % 2 == 0) {
                    g.setColor(getForeground());
                    g.fillPolygon(new int[] {x+1, x+5, x+9}, new int[] {y+6, y+1, y+6}, 3);
                } else {
                    g.setColor(ColorUtils.brighter(getForeground(), .28f));
                    g.drawPolygon(new int[] {x+2, x+5, x+8}, new int[] {y+5, y+2, y+5}, 3);
                }
                if (timesSelected % 2 == 1) {
                    g.setColor(getForeground());
                    g.fillPolygon(new int[] {x+2, x+5, x+9}, new int[] {y+8, y+12, y+8}, 3);
                } else {
                    g.setColor(ColorUtils.brighter(getForeground(), .28f));
                    g.drawPolygon(new int[] {x+2, x+5, x+8}, new int[] {y+8, y+11, y+8}, 3);
                }
            }
            public int getIconWidth() {
                return 10;
            }
            public int getIconHeight() {
                return 13;
            }
        }
        
        int timesSelected;
        
        public PopupMenuItem(final ButtonGroup group) {
            setAction(new AbstractAction("default (id)") {
                private static final long serialVersionUID = 1L;
                public void actionPerformed(ActionEvent e) {
                    if (select(group)) getPanel().defaultSort();
                }
            });
            setSelected(true);
            timesSelected = 1;
            group.add(this);
            setIcon(new PopupMenuItemIcon());
        }
        public PopupMenuItem(final ButtonGroup group, final String id) {
            setAction(new AbstractAction(id) {
                private static final long serialVersionUID = 1L;
                public void actionPerformed(ActionEvent e) {
                    if (select(group)) getPanel().sortOn(id);
                }
            });
            group.add(this);
            setIcon(new PopupMenuItemIcon());
        }
        
        protected boolean select(ButtonGroup group) {
            if (timesSelected++>0) {
                getPanel().reverse();
                return false;
            }
            Enumeration<AbstractButton> buttons = group.getElements();
            while (buttons.hasMoreElements())
                ((PopupMenuItem)buttons.nextElement()).timesSelected = 0;
            timesSelected = 1;
            return true;
        }
    }
    
    public JConstraintPanel getPanel() {
        return panel;
    }
    protected void setPanel(JConstraintPanel panel) {
        this.panel = panel;
    }
}