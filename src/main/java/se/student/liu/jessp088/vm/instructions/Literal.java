package se.student.liu.jessp088.vm.instructions;

/**
 * Pushes arg1 constant to stack.
 */
public class Literal extends OneArgInstruction
{
	public Literal(final int arg) {
		super(arg);
	}

	@Override
	public void process() throws InstructionException {
		push(arg);
	}
}
