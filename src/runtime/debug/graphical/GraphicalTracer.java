package runtime.debug.graphical;

import static java.awt.BorderLayout.SOUTH;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;
import static javax.swing.SwingConstants.CENTER;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import runtime.Constraint;
import runtime.Handler;
import runtime.debug.BasicInterruptingTracer;
import runtime.debug.InterruptableTracer;
import runtime.debug.Tracer;
import util.Terminatable;
import util.comparing.ClassComparator;

/**
 * A graphical tracer, mainly focussed on graphically depicting the 
 * constraint store and the operations applied on it.
 * 
 * @author Peter Van Weert
 */
public class GraphicalTracer extends BasicInterruptingTracer implements Terminatable {

    public static boolean attachTo(Handler handler) {
    	if (!handler.canBeTraced()) return false;
        GraphicalTracer tracer = 
        	new GraphicalTracer(handler.getTracerView().getConstraintClasses());
        handler.setTracer(new InterruptableTracer(tracer));
        tracer.init(handler.getTracerView());
        return true;
    }
    
    private JFrame main;
    
    private JConstraintPanel activeConstraintPanel;
    private Map<String, JConstraintPanel> map 
        = new HashMap<String, JConstraintPanel>();
    
    private Constraint removed;
    
    private Tracer textAreaTracer;
    
    protected GraphicalTracer(Class<? extends Constraint>... constraintClasses) { // cf. above FACTORY METHOD
        super(false, false, true, true, false, true, constraintClasses);
    }
    
    @SuppressWarnings("serial")
    protected void init(Handler handler) {
        main = new JFrame("K.U.Leuven JCHR Graphical Debugger");
        main.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        main.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                terminate();
            }
        });

        JComponent constraintLists = initConstraintLists(handler);
        
        JButton resume = new ActionButton(
            KeyEvent.VK_R,
            new AbstractAction("Resume") {
                public void actionPerformed(ActionEvent e) {
                    resume();
                }
            }
        );
        JButton refresh = new ActionButton(
            KeyEvent.VK_F,
            new AbstractAction("Refresh") {
                @SuppressWarnings("synthetic-access")
                public void actionPerformed(ActionEvent e) {
                    for (JConstraintPanel list : map.values())
                        list.refresh();
                }
            }
        );
        JButton terminate = new ActionButton(
            KeyEvent.VK_S,
            new AbstractAction("Stop") {
                public void actionPerformed(ActionEvent e) {
                    terminate();
                }
            }
        );
        
        Box buttons = new Box(X_AXIS);
        
        buttons.add(resume);
        buttons.add(refresh);
        buttons.add(terminate);
        
        JPanel content = new JPanel(new BorderLayout());
        
        content.add(constraintLists);
        content.add(buttons, SOUTH);

        final JTextArea trace = new JTextArea();
        trace.setEditable(false);
        textAreaTracer = new Highlightor(trace);
        trace.setRows(5);
        
        final JScrollPane scroll = new JScrollPane(trace);
        
        JSplitPane split = new JSplitPane(
            VERTICAL_SPLIT, false, content, scroll
        );
        split.setOneTouchExpandable(true);
        split.setDividerLocation(340);
        
        main.add(split);
        
        int width = Math.max(
            buttons.getPreferredSize().width,
            map.size() * 150
        );
        width = Math.max(width, 400);
        
        width += main.getInsets().left + main.getInsets().right;
        
        main.setSize(width,480);
        main.setVisible(true);
    }
    
    protected JComponent initConstraintLists(Handler handler) {
        Class<? extends Constraint>[] classes = handler.getConstraintClasses();
        
        @SuppressWarnings("unchecked")
        Class<? extends Constraint>[] stored = new Class[classes.length];
        int numStored = 0;
        for (Class<? extends Constraint> clazz : classes)
            if (handler.isStored(clazz)) stored[numStored++] = clazz;
        
        if (numStored > 0) {
        	Arrays.sort(stored, 0, numStored, ClassComparator.getInstance());
        	return initConstraintLists(stored, 0, numStored-1);
        } else {
        	return new JLabel("None of the constraints are ever stored", CENTER);
        }
    }
    
    private JComponent initConstraintLists(Class<? extends Constraint>[] constraints, int from, int to) {
    	if (from == to) {
            Class<? extends Constraint> constraint = constraints[from];
            JConstraintPanel result = new JConstraintPanel(this, constraint);
            map.put(Constraint.getIdentifierOf(constraint), result);
            return result;
        } else {
//            int split = (to - from) / 2;
            int split = from;
            
            JComponent left = initConstraintLists(constraints, from, split);
            JComponent right = initConstraintLists(constraints, split+1, to);
            JSplitPane result = new JSplitPane(
                HORIZONTAL_SPLIT, true, left, right
            );
            
            result.setResizeWeight((double)(split-from+1)/(double)(to-from+1));
            result.setOneTouchExpandable(true);
            
            return result;
        }
    }
    
    protected JConstraintPanel activateConstraintPanel(Constraint constraint) {
        if (hasActiveConstraintPanel())
            getActiveConstraintPanel().deactivate();
        setActiveConstraintPanel(map.get(constraint.getIdentifier()));
        return getActiveConstraintPanel();
    }
    protected JConstraintPanel getActiveConstraintPanel() {
        return activeConstraintPanel;
    }
    protected boolean hasActiveConstraintPanel() {
        return getActiveConstraintPanel() != null;
    }
    protected void setActiveConstraintPanel(JConstraintPanel activeConstraintPanel) {
        this.activeConstraintPanel = activeConstraintPanel;
    }
    
    public void activated(Constraint constraint) {
        if (isTracing()) textAreaTracer.activated(constraint);
    }
    
    public void stored(Constraint constraint) {
        if (isTracing()) {
            textAreaTracer.stored(constraint);
            activateConstraintPanel(constraint).stored(constraint);
        }
    }
    
    public void suspended(Constraint constraint) {
    	if (isTracing()) textAreaTracer.suspended(constraint);
    }
    
    public void reactivated(Constraint constraint) {
        if (isTracing()) {
            textAreaTracer.reactivated(constraint);
            activateConstraintPanel(constraint).reactivated(constraint);
        }
    }
    
    public void removed(Constraint constraint) {
        if (isTracing()) { 
            textAreaTracer.removed(constraint);
            activateConstraintPanel(constraint).removed(constraint);
            removed = constraint;
        }
    }
    
    public void terminated(Constraint constraint) {
        if (isTracing()) textAreaTracer.terminated(constraint);
    }
    
    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
        if (isTracing()) textAreaTracer.fires(ruleId, activeIndex, constraints);
    }
    
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
        if (isTracing()) textAreaTracer.fired(ruleId, activeIndex, constraints);
    }
    
    @Override
    protected void resume() {
        if (isTracing() && removed != null) {
            activateConstraintPanel(removed).doRemove(removed);
            removed = null;
        }
        super.resume();
    }
    
    public void terminate() {
        if (! isTerminated()) {
            main.dispose();
            map.clear();
            main = null;
            
            resume();
        }
    }
    
    public boolean isTracing() {
        return !isTerminated();
    }
    
    public boolean isTerminated() {
        return main == null;
    }
    
    @Override
    public boolean warrantsInterruptIfActivated(Class<? extends Constraint> constraint) {
        return isTracing() && super.warrantsInterruptIfActivated(constraint);
    }
    @Override
    public boolean warrantsInterruptIfSuspended(Class<? extends Constraint> constraint) {
    	return isTracing() && super.warrantsInterruptIfSuspended(constraint);
    }
    @Override
    public boolean warrantsInterruptIfReactivated(Class<? extends Constraint> constraint) {
        return isTracing() && super.warrantsInterruptIfReactivated(constraint);
    }
    @Override
    public boolean warrantsInterruptIfRemoved(Class<? extends Constraint> constraint) {
        return isTracing() && super.warrantsInterruptIfRemoved(constraint);
    }
    @Override
    public boolean warrantsInterruptIfStored(Class<? extends Constraint> constraint) {
        return isTracing() && super.warrantsInterruptIfStored(constraint);
    }
    @Override
    public boolean warrantsInterruptIfTerminated(Class<? extends Constraint> constraint) {
        return isTracing() && super.warrantsInterruptIfTerminated(constraint);
    }
    @Override
    public boolean warrantsInterruptIfFires(String ruleId, int activeIndex, Constraint... constraints) {
        return false;
    }
    @Override
    protected boolean warrantsInterruptIfFired(String ruleId, int activeIndex, Constraint... constraints) {
    	return false;
    }    
}