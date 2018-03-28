package se.student.liu.jessp088.vm.parsing.exceptions;

public class LexerException extends Exception {
	private static final long serialVersionUID = 1L;

	public LexerException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public LexerException(final String arg0) {
		super(arg0);
	}
}
