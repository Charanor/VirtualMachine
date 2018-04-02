package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.InstructionException;

public class Jmp extends JumpInstruction {

	public Jmp(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() {
		return true;
	}
}
