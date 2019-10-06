package runtime.debug.graphical;

import java.awt.TextArea;

import runtime.debug.PlainTextTracer;

public class TextAreaTracer extends PlainTextTracer {

    private TextArea textArea;

    public TextAreaTracer(TextArea textArea) {
        setTextArea(textArea);
    }
    
    public TextArea getTextArea() {
        return textArea;
    }
    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }
    
    @Override
    protected void println(String value) {
        int length = getTextArea().getText().length();
        getTextArea().append(value + LINE_SEPARATOR);
        getTextArea().setCaretPosition(length);
    }
}
