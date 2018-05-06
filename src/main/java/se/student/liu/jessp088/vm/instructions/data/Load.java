package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

/**
 * Loads a variable to the stack.
 */
public class Load extends OneArgInstruction
{

	public Load(final int arg) {
		super(arg);
	}

	@Override
	protected void process() throws InstructionException {
		push(loadVariable(arg));
	}
}
