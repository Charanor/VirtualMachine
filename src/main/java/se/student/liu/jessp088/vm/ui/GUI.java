package se.student.liu.jessp088.vm.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.DefaultVirtualMachine;
import se.student.liu.jessp088.vm.VirtualMachine;
import se.student.liu.jessp088.vm.parsing.Lexer;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.exceptions.LexerException;
import se.student.liu.jessp088.vm.parsing.exceptions.ParserException;

public class GUI {
	private static final int DEFAULT_MAX_STACK_SIZE = 128;
	private static final int DEFAULT_MAX_VARIABLE_SIZE = 128;

	private static final Icon OPEN_ICON = new ImageIcon(GUI.class.getResource("/open_16.png"));
	private static final Icon RUN_ICON = new ImageIcon(GUI.class.getResource("/run_16.png"));
	private static final Icon DEBUG_ICON = new ImageIcon(GUI.class.getResource("/debug_16.png"));
	private static final Icon CONSOLE_ICON = new ImageIcon(GUI.class.getResource("/pc_16.png"));
	private static final Icon VARIABLES_ICON = new ImageIcon(GUI.class.getResource("/var_16.png"));
	private static final Icon PROGRAM_ICON = new ImageIcon(GUI.class.getResource("/vm_16.png"));
	private static final Icon RESUME_ICON = new ImageIcon(GUI.class.getResource("/resume_16.png"));
	private static final Icon PAUSE_ICON = new ImageIcon(GUI.class.getResource("/pause_16.png"));
	private static final Icon STOP_ICON = new ImageIcon(GUI.class.getResource("/stop_16.png"));
	private static final Icon CLOSE_ICON = new ImageIcon(GUI.class.getResource("/close_icon.png"));

	private static final Font CODE_FONT = new Font("Consolas", Font.PLAIN, 12);

	private Lexer lexer;
	private Parser parser;
	private VirtualMachine vm;

	// Debugging state
	private Bytecode parseResult;
	private boolean isDebugging;

	private JFrame frame;
	private JFileChooser fc;
	private JTabbedPane programTabs;
	private JTabbedPane extrasTabs;
	private JTextArea consoleTextArea;
	private JTable variableTable;

	private final int maxStackSize = DEFAULT_MAX_STACK_SIZE;
	private final int maxVariableSize = DEFAULT_MAX_VARIABLE_SIZE;

	/** Launch the application. */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Create the application. */
	public GUI() {
		initialize();
	}

	/** Initialize the contents of the frame. */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Virtual Machine IDE");
		frame.setBounds(100, 100, 820, 540);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lexer = new Lexer();
		parser = new Parser();
		vm = new DefaultVirtualMachine(maxStackSize, maxVariableSize);

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(".vm files", "vm"));
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(".vm files", "vm"));
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		final JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		final JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		final JMenuItem openOption = new JMenuItem("Open...");
		openOption.setIcon(OPEN_ICON);
		openOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openOption.addActionListener(this::openFile);
		fileMenu.add(openOption);

		final JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.addActionListener(
				e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

		final JMenuItem closeOption = new JMenuItem("Close");
		closeOption.setIcon(CLOSE_ICON);
		closeOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		closeOption.addActionListener(this::closeCurrentProgramTab);
		fileMenu.add(closeOption);
		fileMenu.add(exitOption);

		final JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);

		final JMenuItem runOption = new JMenuItem("Run");
		runOption.setIcon(RUN_ICON);
		runOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		runOption.addActionListener(this::runCodeInOpenTab);
		runMenu.add(runOption);

		final JMenuItem debugOption = new JMenuItem("Debug");
		debugOption.setIcon(DEBUG_ICON);
		debugOption.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		debugOption.addActionListener(this::debugCodeInOpenTab);
		runMenu.add(debugOption);

		final JSeparator separator = new JSeparator();
		runMenu.add(separator);

		final JMenuItem resumeOption = new JMenuItem("Resume Execution");
		resumeOption.setEnabled(false);
		resumeOption.setIcon(RESUME_ICON);
		resumeOption.addActionListener(this::resumeDebug);
		runMenu.add(resumeOption);

		final JMenuItem pauseOption = new JMenuItem("Pause Execution");
		pauseOption.setEnabled(false);
		pauseOption.setIcon(PAUSE_ICON);
		runMenu.add(pauseOption);

		final JMenuItem stopOption = new JMenuItem("Stop Execution");
		stopOption.setEnabled(false);
		stopOption.setIcon(STOP_ICON);
		runMenu.add(stopOption);

		final JSeparator separator_1 = new JSeparator();
		runMenu.add(separator_1);

		final JMenuItem configurationOption = new JMenuItem("Run Configuration...");
		runMenu.add(configurationOption);

		final JMenu debugMenu = new JMenu("Debug");
		menuBar.add(debugMenu);

		final JMenuItem addBreakpointOption = new JMenuItem("Add Breakpoint at Cursor");
		addBreakpointOption.addActionListener(this::addBreakpointAtCursor);
		debugMenu.add(addBreakpointOption);
		configurationOption.addActionListener(e -> {
			final RunConfigDialog dialog = new RunConfigDialog(frame);

			dialog.setVisible(true);
		});

		final Object[] columnNames = new Object[] { "Variable", "Value" };

		final JSplitPane programExtrasSplitter = new JSplitPane();
		frame.getContentPane().add(programExtrasSplitter, BorderLayout.CENTER);
		programExtrasSplitter.setResizeWeight(0.8);
		programExtrasSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);

		programTabs = new JTabbedPane(JTabbedPane.TOP);
		programExtrasSplitter.setLeftComponent(programTabs);

		// final JTextArea codeTextArea = new JTextArea();
		// codeTextArea.setBorder(new LineBorder(Color.GRAY));
		// codeTextArea.setFont(CODE_FONT);
		// final JScrollPane codeScrollPane = new
		// JScrollPane(codeTextArea);
		// scrollPane.setRowHeaderView(new
		// TextLineNumber(codeTextArea));
		openNewProgramTab("Program", null);
		// programTabs.addTab("Program", PROGRAM_ICON, codeScrollPane,
		// null);

		extrasTabs = new JTabbedPane(JTabbedPane.TOP);
		programExtrasSplitter.setRightComponent(extrasTabs);

		consoleTextArea = new JTextArea();
		consoleTextArea.setBorder(new LineBorder(Color.GRAY));
		consoleTextArea.setFont(CODE_FONT);
		final JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
		extrasTabs.addTab("New tab", null, consoleScrollPane, null);
		extrasTabs.addTab("Console", CONSOLE_ICON, consoleScrollPane, null);

		variableTable = new JTable();
		variableTable.setFont(CODE_FONT);
		variableTable.setFillsViewportHeight(true);
		variableTable.setEnabled(false);
		variableTable.setModel(new DefaultTableModel(columnNames, 35));
		variableTable.setRowSelectionAllowed(false);

		final JScrollPane variableScrollPane = new JScrollPane(variableTable);
		extrasTabs.addTab("Variables", VARIABLES_ICON, variableScrollPane, null);
	}

	private void openNewProgramTab(final String tabName, final String tabContents) {
		final JTextArea codeTextArea = new JTextArea(tabContents);
		codeTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		codeTextArea.setFont(CODE_FONT);

		final TextLineNumber lineNumbers = new TextLineNumber(codeTextArea);
		final JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
		codeScrollPane.setRowHeaderView(lineNumbers);

		programTabs.addTab(tabName, PROGRAM_ICON, codeScrollPane, null);
	}

	private void openNewExtrasTab(final String tabName, final JComponent tabContents,
			final Icon icon) {
		extrasTabs.addTab(tabName, icon, new JScrollPane(tabContents), null);
	}

	private Bytecode parseCodeInOpenTab() {
		final JTextArea codeArea = getCurrentProgramTextArea();
		if (codeArea == null) return null;
		List<Token> tokens = null;
		try {
			tokens = lexer.tokenize(codeArea.getText());
		} catch (final LexerException e1) {
			addConsoleMessage(String.format("Error during lexing: %s.\n", e1));
			return null;
		}

		Bytecode result = null;
		try {
			result = parser.parse(tokens);
		} catch (final ParserException e1) {
			addConsoleMessage("Error during parsing: %s.\n", e1);
			return null;
		}

		return result;
	}

	private JTextArea getCurrentProgramTextArea() {
		final Component selected = programTabs.getSelectedComponent();
		if (selected == null) return null;
		return (JTextArea) ((JScrollPane) selected).getViewport().getView();
	}

	private void addConsoleMessage(final String format, final Object... args) {
		consoleTextArea.append(String.format(format + "\n", args));
	}

	// Use methods as action listeners
	private void openFile(final ActionEvent e) {
		final int option = fc.showOpenDialog(frame);
		if (option == JFileChooser.APPROVE_OPTION) {
			final File chosen = fc.getSelectedFile();
			try {
				openNewProgramTab(chosen.getName(),
						new String(Files.readAllBytes(chosen.toPath())));
				programTabs.setSelectedIndex(programTabs.getTabCount() - 1);
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void runCodeInOpenTab(final ActionEvent e) {
		final Bytecode result = parseCodeInOpenTab();
		if (result == null) return;
		// if (!vm.execute(result)) {
		// addConsoleMessage("Execution of bytecode failed with error
		// %s.", vm.getError());
		// } else {
		// addConsoleMessage("Execution of bytecode successful. End
		// state: %s.", vm);
		// }
	}

	private void debugCodeInOpenTab(final ActionEvent e) {
		parseResult = parseCodeInOpenTab();
		if (parseResult == null) return;

		isDebugging = true;

		resumeDebug(e);
	}

	private void resumeDebug(final ActionEvent e) {
		// if (vm.isDebugFinished()) {
		// addConsoleMessage("Execution of bytecode successful. End
		// state: %s.", vm);
		// isDebugging = false;
		// return;
		// } else if (!vm.execute(parseResult.code,
		// parseResult.instructionToLineNumber)) {
		// addConsoleMessage("Execution of bytecode failed with error
		// %s.", vm.getError());
		// isDebugging = false;
		// }
	}

	private void closeCurrentProgramTab(final ActionEvent e) {
		final int selectedIdx = programTabs.getSelectedIndex();
		if (selectedIdx < 0) return;
		if (selectedIdx == 0 && programTabs.getTabCount() > 1) {
			programTabs.setSelectedIndex(selectedIdx + 1);
		} else
			programTabs.setSelectedIndex(selectedIdx - 1);
		programTabs.removeTabAt(selectedIdx);
	}

	private void addBreakpointAtCursor(final ActionEvent e) {
		try {
			final JTextArea text = getCurrentProgramTextArea();
			if (text == null) return;
			final int carretOffset = text.getCaretPosition();
			final int lineNumber = text.getLineOfOffset(carretOffset);
			// vm.setBreakpoint(lineNumber);
			addConsoleMessage("Setting breakpoint at line %s.", lineNumber);
		} catch (final BadLocationException e1) {
			addConsoleMessage("Could not add breakpoint at cursor. Error: %s.", e1);
		}
	}

}
