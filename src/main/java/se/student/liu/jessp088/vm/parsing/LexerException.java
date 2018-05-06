package se.student.liu.jessp088.vm.parsing;

/**
 * Exception thrown by the Lexer to signal that something went wrong
 * during lexing.
 */
public class LexerException extends Exception
{
	private static final long serialVersionUID = 1L;

	public LexerException(final String arg0) {
		super(arg0);
	}
}
