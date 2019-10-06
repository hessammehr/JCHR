package runtime.debug.graphical;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;

import runtime.Constraint;

public final class Highlightor extends JTextAreaTracer {
    
    public Highlightor(JTextArea textArea) {
        super(textArea);
    }

    private HighlightPainter painter;
    
    private final HighlightPainter CYAN 
        = new DefaultHighlightPainter(Color.CYAN);
    private final HighlightPainter YELLOW 
        = new DefaultHighlightPainter(Color.YELLOW);
    private final HighlightPainter RED 
        = new DefaultHighlightPainter(Color.RED);
    private final HighlightPainter GREEN 
        = new DefaultHighlightPainter(Color.GREEN);
    private final HighlightPainter GRAY
    	= new DefaultHighlightPainter(Color.GRAY);
    
    @Override
    public void activated(Constraint constraint) {
        painter = CYAN;
        super.activated(constraint);
    }
    
    @Override
    public void suspended(Constraint constraint) {
    	painter = GRAY;
    	super.suspended(constraint);
    }
    
    @Override
    public void reactivated(Constraint constraint) {
        painter = CYAN;
        super.reactivated(constraint);
    }
    
    @Override
    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
        painter = YELLOW;
        super.fires(ruleId, activeIndex, constraints);
    }
    
    @Override
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
        painter = YELLOW;
        super.fired(ruleId, activeIndex, constraints);
    }

    @Override
    public void removed(Constraint constraint) {
        painter = RED;
        super.removed(constraint);
    }

    @Override
    public void stored(Constraint constraint) {
        painter = GREEN;
        super.stored(constraint);
    }

    @Override
    public void terminated(Constraint constraint) {
        painter = RED; 
        super.terminated(constraint);
    }

    @Override
    protected void println(String value) {
        int start = getTextArea().getDocument().getLength();
        super.println(value);
        try {
            if (painter != null) 
                getTextArea().getHighlighter().addHighlight(
                    start, start + value.indexOf(']') + 1, 
                    painter
                );
        } catch (BadLocationException e) {
            throw new InternalError();
        }
    }
}