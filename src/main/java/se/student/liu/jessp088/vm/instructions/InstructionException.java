package se.student.liu.jessp088.vm.instructions;

/**
 * Exception thrown by {@link Instruction} to signal that the execution of an instrucion went horribly wrong.
 */
public class InstructionException extends Exception
{
	private static final long serialVersionUID = 1L;

	public InstructionException(final String format, final Object... args) {
		super(String.format(format, args));
	}
}
