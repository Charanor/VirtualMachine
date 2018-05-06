package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Jumps to an instruction if the top value of the stack is greater than or equal to zero.
 */
public class JmpGE extends JumpInstruction
{
	public JmpGE(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		return pop() >= 0;
	}
}
