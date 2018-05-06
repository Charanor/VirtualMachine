package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Jumps to an instruction if the top value of the stack is greater than zero.
 */
public class JmpGT extends JumpInstruction
{

	public JmpGT(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		return pop() > 0;
	}
}
