package se.student.liu.jessp088.vm.instructions.control.subroutine;

import se.student.liu.jessp088.vm.VirtualMachine;
import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

public class Return extends Instruction {
	@Override
	protected void process() throws InstructionException {
		final int value = pop();
		final int address = pop();

		if (VirtualMachine.DEBUG) ;// System.out.println("Returning "
									// + value + " to " + address);
		push(value);
		setPtr(address);
	}
}
