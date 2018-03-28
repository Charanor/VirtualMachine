package se.student.liu.jessp088.vm.instructions;

public class InstructionException extends Exception {
	private static final long serialVersionUID = 1L;

	public InstructionException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public InstructionException(final String arg0) {
		super(arg0);
	}
}
