package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

/**
 * Subclass of {@link se.student.liu.jessp088.vm.instructions.Instruction} and the base of all jump instructions.
 */
public abstract class JumpInstruction extends OneArgInstruction
{
	protected JumpInstruction(final int arg) {
		super(arg);
	}

	protected abstract boolean shouldJump() throws InstructionException;

	@Override
	public void process() throws InstructionException {
		if (shouldJump()) setPtr(arg);
	}
}
