package se.student.liu.jessp088.vm.instructions.control.subroutine;

import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.control.JumpInstruction;

public class Call extends JumpInstruction {
	public Call(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() throws InstructionException {
		push(getPtr());
		return true;
	}
}
