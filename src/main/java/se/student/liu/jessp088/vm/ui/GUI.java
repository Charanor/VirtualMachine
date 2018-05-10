package se.student.liu.jessp088.vm.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.DebugListener;
import se.student.liu.jessp088.vm.DefaultVirtualMachine;
import se.student.liu.jessp088.vm.Variables;
import se.student.liu.jessp088.vm.VirtualMachine;
import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.data.Store;
import se.student.liu.jessp088.vm.parsing.Lexer;
import se.student.liu.jessp088.vm.parsing.LexerException;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.ParserException;
import se.student.liu.jessp088.vm.parsing.Token;

/**
 * The GUI window also known as the "IDE". Takes care of constructing and showing the main window
 * and serves as the entry point into the program.
 */
public class GUI
{
	private static final int DEFAULT_MAX_STACK_SIZE = 128;
	private static final int DEFAULT_MAX_VARIABLE_SIZE = 128;

	private static final Icon OPEN_ICON = new ImageIcon(GUI.class.getResource("/open_16.png"));
	private static final Icon RUN_ICON = new ImageIcon(GUI.class.getResource("/run_16.png"));
	private static final Icon DEBUG_ICON = new ImageIcon(GUI.class.getResource("/debug_16.png"));
	private static final Icon CONSOLE_ICON = new ImageIcon(GUI.class.getResource("/pc_16.png"));
	private static final Icon VARIABLES_ICON = new ImageIcon(GUI.class.getResource("/var_16.png"));
	private static final Icon STACK_ICON = new ImageIcon(GUI.class.getResource("/stack_16.png"));
	private static final Icon PROGRAM_ICON = new ImageIcon(GUI.class.getResource("/vm_16.png"));
	private static final Icon RESUME_ICON = new ImageIcon(GUI.class.getResource("/resume_16.png"));
	private static final Icon PAUSE_ICON = new ImageIcon(GUI.class.getResource("/pause_16.png"));
	private static final Icon STOP_ICON = new ImageIcon(GUI.class.getResource("/stop_16.png"));
	private static final Icon CLOSE_ICON = new ImageIcon(GUI.class.getResource("/close_icon.png"));

	private static final Font CODE_FONT = new Font("Consolas", Font.PLAIN, 12);

	private static final int WINDOW_WIDTH = 820;
	private static final int WINDOW_HEIGHT = 540;
	private static final double PERCENT_HEIGHT = 0.8;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Lexer lexer;
	private Parser parser;
	private VirtualMachine vm;
	private boolean step;
	private boolean wasPaused;
	private boolean autoClear;

	private JFrame frame;
	private JFileChooser fc;
	private JTabbedPane programTabs;
	private JTextArea consoleTextArea;
	private JTable variableTable;

	private JMenuItem resumeOption;
	private JMenuItem pauseOption;
	private JMenuItem stopOption;
	private JTextArea stackTextArea;
	private JLabel lineInfoLabel;
	private JSplitPane programExtrasSplitter;

	/** Launch the application. */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run() {
				final GUI window = new GUI();
				window.frame.setVisible(true);
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
		frame.setBounds(100, 100, WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		lexer = new Lexer();
		parser = new Parser();
		vm = new DefaultVirtualMachine(DEFAULT_MAX_STACK_SIZE, DEFAULT_MAX_VARIABLE_SIZE);

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(".vm files", "vm"));
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(".vm files", "vm"));
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		setupMenuBar();

		programExtrasSplitter = new JSplitPane();
		frame.getContentPane().add(programExtrasSplitter, BorderLayout.CENTER);
		programExtrasSplitter.setResizeWeight(PERCENT_HEIGHT);
		programExtrasSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);

		programTabs = new JTabbedPane(SwingConstants.TOP);

		openNewProgramTab("Program", null);

		setupExtrasTab();

		// Redirect logging to GUI console
		// Closed below in the WindowListener
		final PrintStream newOut = new PrintStream(new Console(consoleTextArea));
		final PrintStream oldOut = System.out;
		System.setOut(newOut);

		final JPanel codePanel = new JPanel();
		programExtrasSplitter.setLeftComponent(codePanel);
		codePanel.setLayout(new BorderLayout(0, 0));
		codePanel.add(programTabs);

		lineInfoLabel = new JLabel("Line: 0, Column: 0");
		codePanel.add(lineInfoLabel, BorderLayout.SOUTH);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(final WindowEvent e) {
				System.setOut(oldOut); // Restore PrintStream
				newOut.close(); // Close the new PrintStream
				e.getWindow().dispose(); // Dispose of the window
			}
		});

		// Listener to add variables to table
		vm.addDebugListener(new DebugListener()
		{
			@Override
			public void beforeExecution(final VirtualMachine vm) {
				final int numRows = variableTable.getModel().getRowCount();
				for (int i = 0; i < numRows; i++) {
					variableTable.getModel().setValueAt(null, i, 0);
					variableTable.getModel().setValueAt(null, i, 1);
				}

				stackTextArea.setText("");
			}

			@Override
			public void afterExecution(final VirtualMachine vm) {
				resumeOption.setEnabled(false);
				pauseOption.setEnabled(false);
				stopOption.setEnabled(false);
			}

			@Override
			public void beforeInstruction(final VirtualMachine vm) {
				if (step && !wasPaused) {
					vm.pauseExecution();
					wasPaused = true;
				} else wasPaused = false;
			}

			@Override
			public void afterInstruction(final VirtualMachine vm) {
				stackTextArea.append(vm.getStack().toString());
				stackTextArea.append("\n");

				final Instruction ins = vm.getCurrentInstruction();
				// We have to check for a Store instruction so we can update the
				// varable table. The alternative would be to update the table with all variables
				// all the time, but this would consume unecessary resources.
				if (!(ins instanceof Store)) return;
				final Store store = (Store) ins;
				final int varIdx = store.getArg();

				final Variables variables = vm.getVariables();
				variableTable.getModel().setValueAt(Integer.valueOf(varIdx), varIdx, 0);
				variableTable.getModel().setValueAt(Integer.valueOf(variables.load(varIdx)), varIdx, 1);
			}

			@Override
			public void onStateChanged(final VirtualMachine vm) {
				resumeOption.setEnabled(vm.isPaused());
				pauseOption.setEnabled(vm.isRunning());
				stopOption.setEnabled(vm.isPaused() || vm.isRunning());
			}
		});
	}

	private void setupMenuBar() {
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
		exitOption.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

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
		debugOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		debugOption.addActionListener(this::debugCodeInOpenTab);
		runMenu.add(debugOption);

		final JSeparator separator = new JSeparator();
		runMenu.add(separator);

		resumeOption = new JMenuItem("Resume Execution");
		resumeOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		resumeOption.setEnabled(false);
		resumeOption.setIcon(RESUME_ICON);
		resumeOption.addActionListener(this::resumeExecution);
		runMenu.add(resumeOption);

		pauseOption = new JMenuItem("Pause Execution");
		pauseOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		pauseOption.setEnabled(false);
		pauseOption.setIcon(PAUSE_ICON);
		pauseOption.addActionListener(this::pauseExecution);
		runMenu.add(pauseOption);

		stopOption = new JMenuItem("Stop Execution");
		stopOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		stopOption.setEnabled(false);
		stopOption.setIcon(STOP_ICON);
		stopOption.addActionListener(this::stopExecution);
		runMenu.add(stopOption);

		final JSeparator separator1 = new JSeparator();
		runMenu.add(separator1);

		final JMenu debugMenu = new JMenu("Debug");
		menuBar.add(debugMenu);

		final JMenuItem addBreakpointOption = new JMenuItem("Add Breakpoint at Cursor");
		addBreakpointOption.addActionListener(this::addBreakpointAtCursor);
		debugMenu.add(addBreakpointOption);

		final JMenuItem removeBreakpointOption = new JMenuItem("Remove Breakpoint at Cursor");
		removeBreakpointOption.addActionListener(this::removeBreakpointAtCursor);
		debugMenu.add(removeBreakpointOption);

		final JMenuItem removeAllOption = new JMenuItem("Remove all Breakpoints");
		removeAllOption.addActionListener(e -> vm.removeAllBreakpoints());
		debugMenu.add(removeAllOption);

		step = false;
		wasPaused = false;
		final JCheckBoxMenuItem toggleStepOption = new JCheckBoxMenuItem("Step code");
		toggleStepOption.addActionListener(e -> step = toggleStepOption.isSelected());

		final JMenu consoleMenuOption = new JMenu("Console...");
		debugMenu.add(consoleMenuOption);

		final JMenuItem clearConsoleOption = new JMenuItem("Clear console");
		consoleMenuOption.add(clearConsoleOption);

		this.autoClear = false;
		final JCheckBoxMenuItem autoClearOption = new JCheckBoxMenuItem("Clear console on Run");
		autoClearOption.addActionListener(e -> this.autoClear = autoClearOption.isSelected());
		consoleMenuOption.add(autoClearOption);
		clearConsoleOption.addActionListener(e -> consoleTextArea.setText(""));
		debugMenu.add(toggleStepOption);
	}

	private void setupExtrasTab() {
		final JTabbedPane extrasTabs = new JTabbedPane(SwingConstants.TOP);
		programExtrasSplitter.setRightComponent(extrasTabs);

		consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		consoleTextArea.setBorder(new LineBorder(Color.GRAY));
		consoleTextArea.setFont(CODE_FONT);
		final JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
		extrasTabs.addTab("New tab", null, consoleScrollPane, null);
		extrasTabs.addTab("Console", CONSOLE_ICON, consoleScrollPane, null);


		final Object[] columnNames = new Object[] { "Variable", "Value" };
		variableTable = new JTable();
		variableTable.setFont(CODE_FONT);
		variableTable.setFillsViewportHeight(true);
		variableTable.setEnabled(false);
		variableTable.setModel(new DefaultTableModel(columnNames, 35)); // Arbitrary number of rows (expanded as it goes)
		variableTable.setRowSelectionAllowed(false);

		final JScrollPane variableScrollPane = new JScrollPane(variableTable);
		extrasTabs.addTab("Variables", VARIABLES_ICON, variableScrollPane, null);

		stackTextArea = new JTextArea();
		stackTextArea.setEditable(false);
		final JScrollPane stackScrollPane = new JScrollPane(stackTextArea);
		extrasTabs.addTab("Stack", STACK_ICON, stackScrollPane, null);
	}

	private void openNewProgramTab(final String tabName, final String tabContents) {
		final JTextArea text = new JTextArea(tabContents);
		text.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		text.setFont(CODE_FONT);
		text.addCaretListener(e -> {
			try {
				final int lineNumber = lineNumberAtCursor();
				final int column = text.getCaretPosition() - text.getLineStartOffset(lineNumber - 1);
				lineInfoLabel.setText("Line: " + lineNumber + ", Column: " + column);
			} catch (final BadLocationException ignored) {
				// We don't care about the exception, it's not fatal and we cannot
				// do anything to recover anyways.
			}
		});

		final JScrollPane codeScrollPane = new JScrollPane(text);
		programTabs.addTab(tabName, PROGRAM_ICON, codeScrollPane, null);
	}

	private Bytecode parseCodeInOpenTab() {
		final JTextArea codeArea = getCurrentProgramTextArea();
		if (codeArea == null) return null;
		List<Token> tokens;
		try {
			tokens = lexer.tokenize(codeArea.getText());
		} catch (final LexerException e1) {
			addConsoleMessage("Error during lexing: %s.\n", e1);
			return null;
		}

		Bytecode result;
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
		consoleTextArea.append(String.format(format, args));
		consoleTextArea.append("\n");
	}

	////////// Use methods as action listeners. ActionEvent e is present only because it is required for
	////////// method reference to work in this case.

	private void openFile(final ActionEvent e) {
		final int option = fc.showOpenDialog(frame);
		if (option == JFileChooser.APPROVE_OPTION) {
			final File chosen = fc.getSelectedFile();
			try {
				openNewProgramTab(chosen.getName(), new String(Files.readAllBytes(chosen.toPath())));
				programTabs.setSelectedIndex(programTabs.getTabCount() - 1);
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void runCodeInOpenTab(final ActionEvent e) {
		final Bytecode result = parseCodeInOpenTab();
		if (result == null) return;

		if (autoClear) consoleTextArea.setText("");
		executor.execute(() -> {
			vm.stopExecution();
			vm.execute(result);
		});
	}

	private void debugCodeInOpenTab(final ActionEvent e) {
		final Bytecode result = parseCodeInOpenTab();
		if (result == null) return;

		if (autoClear) consoleTextArea.setText("");
		executor.execute(() -> {
			vm.stopExecution();
			vm.debug(result);
		});
	}

	private void resumeExecution(final ActionEvent e) {
		vm.resumeExecution();
	}

	private void pauseExecution(final ActionEvent e) {
		vm.pauseExecution();
	}

	private void stopExecution(final ActionEvent e) {
		vm.stopExecution();
	}

	private void closeCurrentProgramTab(final ActionEvent e) {
		final int selectedIdx = programTabs.getSelectedIndex();
		if (selectedIdx < 0) return;
		if (selectedIdx == 0 && programTabs.getTabCount() > 1) {
			programTabs.setSelectedIndex(selectedIdx + 1);
		} else programTabs.setSelectedIndex(selectedIdx - 1);
		programTabs.removeTabAt(selectedIdx);
	}

	private void addBreakpointAtCursor(final ActionEvent e) {
		final int lineNumber = lineNumberAtCursor();
		if (lineNumber == -1) {
			addConsoleMessage("Could not add breakpoint at cursor, invalid line.");
			return;
		}

		vm.addBreakpoint(vm.convertLineToPtr(lineNumber));
		addConsoleMessage("Setting breakpoint at line %s.", Integer.valueOf(lineNumber));
	}

	private void removeBreakpointAtCursor(final ActionEvent e) {
		final int lineNumber = lineNumberAtCursor();
		if (lineNumber == -1) {
			addConsoleMessage("Could not remove breakpoint at cursor, invalid line.");
			return;
		}

		vm.removeBreakpoint(vm.convertLineToPtr(lineNumber));
		addConsoleMessage("Removing breakpoint at line %s.", Integer.valueOf(lineNumber));
	}

	private int lineNumberAtCursor() {
		try {
			final JTextArea text = getCurrentProgramTextArea();
			if (text == null) return -1;
			final int carretOffset = text.getCaretPosition();
			return text.getLineOfOffset(carretOffset) + 1;
		} catch (final BadLocationException e1) {
			addConsoleMessage("Could not fetch line number at cursor. Error: %s.", e1);
			return -1;
		}
	}

}
