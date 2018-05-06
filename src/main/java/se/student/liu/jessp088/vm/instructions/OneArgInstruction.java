package se.student.liu.jessp088.vm.instructions;

/**
 * Subclass of {@link Instruction} that takes one argument as a parameter.
 */
public abstract class OneArgInstruction extends Instruction
{
	protected final int arg;

	protected OneArgInstruction(final int arg) {
		this.arg = arg;
	}

	public int getArg() {
		return arg;
	}

	@Override
	public String toString() {
		return super.toString() + " " + arg;
	}
}
