package runtime.debug.graphical;

import javax.swing.JTextArea;

import runtime.debug.PlainTextTracer;

public class JTextAreaTracer extends PlainTextTracer {

    private JTextArea textArea;

    public JTextAreaTracer(JTextArea textArea) {
        setTextArea(textArea);
    }
    
    public JTextArea getTextArea() {
        return textArea;
    }
    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }
    
    @Override
    protected void println(String value) {
        int length = getTextArea().getDocument().getLength();
        getTextArea().append(value + LINE_SEPARATOR);
        getTextArea().setCaretPosition(length);
    }
}
