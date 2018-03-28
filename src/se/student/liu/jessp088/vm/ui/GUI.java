package se.student.liu.jessp088.vm.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.VirtualMachine;

public class GUI {
	private Preprocessor preprocessor;
	private Compiler compiler;
	private VirtualMachine vm;

	private JFrame frame;
	private JFileChooser fc;
	private CodeAndConsole codeAndConsole;
	private JTable table;

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
		frame.setBounds(100, 100, 820, 540);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		final JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		fc = new JFileChooser();
		final JMenuItem openOption = new JMenuItem("Open...");
		openOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openOption.addActionListener((a) -> {
			fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setFileFilter(new FileNameExtensionFilter(".vm Bytecode Files", "vm"));

			final int v = fc.showOpenDialog(null);
			if (v == JFileChooser.APPROVE_OPTION) {
				final File file = fc.getSelectedFile();
				try {
					codeAndConsole.setCode(
							new String(Files.readAllBytes(file.toPath())).replace("\t", "    "));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		fileMenu.add(openOption);

		final JMenuItem exitOption = new JMenuItem("Exit");
		fileMenu.add(exitOption);

		final JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);

		final JMenuItem runOption = new JMenuItem("Run");
		runOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		runOption.addActionListener(e -> runCode());
		runMenu.add(runOption);

		final JMenuItem debugOption = new JMenuItem("Debug");
		debugOption.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		runMenu.add(debugOption);

		final JMenu settingsOption = new JMenu("Settings");
		runMenu.add(settingsOption);

		final ButtonGroup settingsGroup = new ButtonGroup();

		final JRadioButtonMenuItem preprocessRadio = new JRadioButtonMenuItem("Preprocess only");
		settingsOption.add(preprocessRadio);

		final JRadioButtonMenuItem compileRadio = new JRadioButtonMenuItem("Compile only");
		settingsOption.add(compileRadio);

		final JRadioButtonMenuItem preprocessAndCompileRadio = new JRadioButtonMenuItem(
				"Preprocess and Compile");
		settingsOption.add(preprocessAndCompileRadio);
		preprocessAndCompileRadio.setSelected(true);

		settingsGroup.add(preprocessRadio);
		settingsGroup.add(compileRadio);
		settingsGroup.add(preprocessAndCompileRadio);
		table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] { { "i", null }, },
				new String[] { "Variable", "Value" }) {
			Class[] columnTypes = new Class[] { String.class, Integer.class };

			@Override
			public Class getColumnClass(final int columnIndex) {
				return columnTypes[columnIndex];
			}

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		});
		table.setRowSelectionAllowed(false);

		final JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.EAST);

		codeAndConsole = new CodeAndConsole();
		codeAndConsole.setContinuousLayout(true);
		frame.getContentPane().add(codeAndConsole, BorderLayout.CENTER);

		preprocessor = new Preprocessor();
		compiler = new Compiler(preprocessor);
		vm = new VirtualMachine(128, 128, true);
	}

	private void runCode() {
		final PrintStream original = System.out;

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream redirectStream = new PrintStream(baos);

		System.out.println("Running code...");
		System.setOut(redirectStream);

		final String processed = preprocessor.process(codeAndConsole.getCode());
		final Bytecode bytecode = compiler.compile(processed);
		vm.execute(bytecode);

		redirectStream.flush();
		codeAndConsole.setConsole(baos.toString());
		System.setOut(original);

		System.out.println("End of running code...");
	}

}
