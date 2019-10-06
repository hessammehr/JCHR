package runtime.debug.graphical;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class ActionButton extends JButton {
    private static final long serialVersionUID = 1L;

    public ActionButton(int mnemonic, Action a) {
        super(a);
        setMnemonic(mnemonic);
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
    }
}
