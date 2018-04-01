package se.student.liu.jessp088.vm.ui;

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LabeledTextArea extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JLabel label;
	private final JTextArea textArea;

	public LabeledTextArea(final String text, final boolean textAreaEditable,
			final boolean scrollTextArea) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(label = new JLabel(text));
		textArea = new JTextArea();
		if (scrollTextArea) {
			this.add(new JScrollPane(textArea));
		} else {
			this.add(textArea);
		}

		textArea.setEditable(textAreaEditable);
	}

	public void setTextAreaFont(final Font font) {
		textArea.setFont(font);
		textArea.setCaretPosition(0);
	}

	public void setLabelFont(final Font font) {
		label.setFont(font);
	}

	public void setTextAreaText(final String text) {
		textArea.setText(text);
	}

	public String getTextAreaText() {
		return textArea.getText();
	}

	public void setLabelText(final String text) {
		this.label.setText(text);
	}

	public String getLabelText() {
		return label.getText();
	}
}
