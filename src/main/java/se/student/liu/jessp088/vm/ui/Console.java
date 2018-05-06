package se.student.liu.jessp088.vm.ui;

import java.io.OutputStream;

import javax.swing.JTextArea;

/** {@link OutputStream} that writes to a {@link JTextArea}.
 *
 * @author Charanor */
public class Console extends OutputStream {
	private final JTextArea textArea;

	public Console(final JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(final int b) {
		final char c = (char) b;
		textArea.append(String.valueOf(c));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
