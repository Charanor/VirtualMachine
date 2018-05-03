package se.student.liu.jessp088.vm.parsing;

public class ParserException extends Exception {
	private static final long serialVersionUID = 1L;

	public ParserException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ParserException(final String format, final Object... args) {
		super(String.format(format, args));
	}
}
