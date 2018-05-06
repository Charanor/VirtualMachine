package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

/**
 * Stores a variable from the stack.
 */
public class Store extends OneArgInstruction
{

	public Store(final int arg) {
		super(arg);
	}

	@Override
	protected void process() throws InstructionException {
		storeVariable(arg, pop());
	}
}
