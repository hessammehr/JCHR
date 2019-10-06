package util;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import static util.StringUtils.*;

public class StringTool {

	public static void main(String[] args) {
		JFrame frame = new JFrame("String Tool");
		
		frame.setLayout(new GridLayout(3,1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTextArea in, out;
		final JButton button;
		frame.add(in = new JTextArea());
		frame.add(button = new JButton(""));
		frame.add(out = new JTextArea());
		
		out.setEditable(false);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String nl = getLineSeparator();
				out.setText("");
				StringTokenizer tokenizer = new StringTokenizer(in.getText(), nl);
				if (tokenizer.hasMoreTokens()) do {
					out.append('"' + javaEscape(tokenizer.nextToken()) + '"');
					if (tokenizer.hasMoreTokens())
						out.append(',' + nl);
					else
						break;
				} while (true);
			}
		});
		
		frame.setVisible(true);
	}
}
