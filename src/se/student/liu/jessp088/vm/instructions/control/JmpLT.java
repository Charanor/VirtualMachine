package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

public class JmpLT extends JumpInstruction {

	public JmpLT(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		return pop() < 0;
	}
}
