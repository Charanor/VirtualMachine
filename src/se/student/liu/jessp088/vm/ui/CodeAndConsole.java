package se.student.liu.jessp088.vm.ui;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class CodeAndConsole extends JSplitPane {
	private static final long serialVersionUID = 1L;

	private final JTextArea codeArea;
	private final JTextArea consoleArea;

	public CodeAndConsole() {
		codeArea = new JTextArea();
		codeArea.setEditable(false);
		codeArea.setFont(new Font("Consolas", Font.PLAIN, 12));

		consoleArea = new JTextArea();
		consoleArea.setEditable(false);

		final JScrollPane codeAreaScrollPane = new JScrollPane(codeArea);
		final JScrollPane consoleAreaScrollPane = new JScrollPane(consoleArea);

		setResizeWeight(0.8);
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setLeftComponent(codeAreaScrollPane);
		setRightComponent(consoleAreaScrollPane);
	}

	public void setCode(final String code) {
		codeArea.setText(code);
		codeArea.setCaretPosition(0);
	}

	public void setConsole(final String text) {
		consoleArea.setText(text);
		consoleArea.setCaretPosition(0);
	}

	public String getCode() {
		return codeArea.getText();
	}

	public String getConsole() {
		return consoleArea.getText();
	}
}
