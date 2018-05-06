package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Jumps to an instruction if the top value of the stack is equal to zero.
 */
public class JmpEQ extends JumpInstruction
{

	public JmpEQ(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		return pop() == 0;
	}
}
