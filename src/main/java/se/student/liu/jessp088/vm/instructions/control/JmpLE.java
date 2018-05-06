package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Jumps to an instruction if the top value of the stack is less than or equal to zero.
 */
public class JmpLE extends JumpInstruction
{
	public JmpLE(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		return pop() <= 0;
	}
}
