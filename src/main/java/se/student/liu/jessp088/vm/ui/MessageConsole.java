package se.student.liu.jessp088.vm.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/*
 *  Create a simple console to display text messages.
 *
 *  Messages can be directed here from different sources. Each source can
 *  have its messages displayed in a different color.
 *
 *  Messages can either be appended to the console or inserted as the first
 *  line of the console
 *
 *  You can limit the number of lines to hold in the Document.
 */
public class MessageConsole {
	private final JTextComponent textComponent;
	private final Document document;

	public MessageConsole(final JTextComponent textComponent) {
		this.textComponent = textComponent;
		this.document = textComponent.getDocument();
		textComponent.setEditable(false);
	}

	/* Redirect the output from the standard output to the console using the default text color and
	 * null PrintStream */
	public void redirectOut() {
		final ConsoleOutputStream cos = new ConsoleOutputStream();
		System.setOut(new PrintStream(cos, true));
	}

	/* Class to intercept output from a PrintStream and add it to a Document. The output can
	 * optionally be redirected to a different PrintStream. The text displayed in the Document can
	 * be color coded to indicate the output source. */
	public class ConsoleOutputStream extends ByteArrayOutputStream {
		private final String EOL = System.getProperty("line.separator");
		private final StringBuffer buffer = new StringBuffer(80);
		private boolean isFirstLine;

		/* Override this method to intercept the output text. Each line of text output will actually
		 * involve invoking this method twice: a) for the actual text message b) for the newLine
		 * string The message will be treated differently depending on whether the line will be
		 * appended or inserted into the Document */
		@Override
		public void flush() {
			final String message = toString();
			if (message.isEmpty()) return;
			handleAppend(message);
			reset();
		}

		/* We don't want to have blank lines in the Document. The first line added will simply be
		 * the message. For additional lines it will be: newLine + message */
		private void handleAppend(final String message) {
			// This check is needed in case the text in the Document has been
			// cleared. The buffer may contain the EOL string from the previous
			// message.

			if (document.getLength() == 0) buffer.setLength(0);

			buffer.append(message);
			if (!EOL.equals(message)) clearBuffer();
		}

		/* The message and the newLine have been added to the buffer in the appropriate order so we
		 * can now update the Document and send the text to the optional PrintStream. */
		private void clearBuffer() {
			// In case both the standard out and standard err are being redirected
			// we need to insert a newline character for the first line only

			if (isFirstLine && document.getLength() != 0) {
				buffer.insert(0, "\n");
			}

			isFirstLine = false;
			final String line = buffer.toString();

			try {
				final int offset = document.getLength();
				document.insertString(offset, line, null);
				textComponent.setCaretPosition(document.getLength());
			} catch (final BadLocationException ignored) {
			}

			buffer.setLength(0);
		}
	}
}
