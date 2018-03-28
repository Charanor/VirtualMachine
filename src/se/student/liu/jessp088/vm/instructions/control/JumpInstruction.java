package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.VirtualMachine;
import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

public abstract class JumpInstruction extends OneArgInstruction {
	public JumpInstruction(final int arg) {
		super(arg);
	}

	protected abstract boolean shouldJump() throws InstructionException;

	@Override
	public void process() throws InstructionException {
		if (shouldJump()) {
			if (VirtualMachine.DEBUG) ;// System.out.println("Jumping
										// from " + (getPtr() - 1) + "
										// to " + arg);
			setPtr(arg);
		}
	}
}
