package se.student.liu.jessp088.vm.instructions.control.subroutine;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Returns from a subroutine.
 */
public class Break extends Instruction
{
	@Override
	protected void process() throws InstructionException {
		setPtr(pop());
	}
}
