package se.student.liu.jessp088.vm.exceptions;

public class CompilationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompilationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CompilationException(final String message) {
		super(message);
	}

}
